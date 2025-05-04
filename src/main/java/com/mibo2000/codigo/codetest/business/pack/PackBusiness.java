package com.mibo2000.codigo.codetest.business.pack;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.ResponseStatus;
import com.mibo2000.codigo.codetest.business.BaseBusiness;
import com.mibo2000.codigo.codetest.exception.BadRequestException;
import com.mibo2000.codigo.codetest.exception.UnexpectedException;
import com.mibo2000.codigo.codetest.mapper.PackageMapper;
import com.mibo2000.codigo.codetest.mapper.UserPackageMapper;
import com.mibo2000.codigo.codetest.modal.dto.pack.PackageResponse;
import com.mibo2000.codigo.codetest.modal.dto.pack.UserPackageDto;
import com.mibo2000.codigo.codetest.modal.dto.pack.UserPackageResponse;
import com.mibo2000.codigo.codetest.modal.entity.PackageEntity;
import com.mibo2000.codigo.codetest.modal.entity.PackagePurchaseHistoryEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;
import com.mibo2000.codigo.codetest.modal.repo.PackagePurchaseHistoryRepo;
import com.mibo2000.codigo.codetest.modal.repo.PackageRepo;
import com.mibo2000.codigo.codetest.modal.repo.UserPackageRepo;
import com.mibo2000.codigo.codetest.service.payment.PaymentService;
import com.mibo2000.codigo.codetest.service.schedule.PackageExpiryReminderScheduler;
import com.mibo2000.codigo.codetest.util.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PackBusiness extends BaseBusiness implements IPack {
    private final PackageExpiryReminderScheduler scheduler;
    private final PackageRepo packageRepo;
    private final UserPackageRepo userPackageRepo;
    private final PackageMapper packageMapper;
    private final UserPackageMapper userPackageMapper;
    private final PaymentService paymentService;
    private final CommonUtility commonUtility;
    private final PackagePurchaseHistoryRepo packagePurchaseHistoryRepo;

    @Override
    public BaseResponse<List<PackageResponse>> getAvailablePacks(String packageName, String countryCode, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request) {
        getUserEntity(request);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, elementCount, Sort.by(sortDirection, sortBy));
        Page<PackageEntity> page = this.packageRepo.getPackagePage(packageName, countryCode, pageable);
        return this.commonUtility.convertFromPage(page, this.packageMapper::toDtoList);
    }

    @Override
    public BaseResponse<?> getAvailablePackDetail(String packId, HttpServletRequest request) {
        getUserEntity(request);
        PackageEntity packageEntity = this.packageRepo.findById(UUID.fromString(packId)).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "No Package Found"));
        return BaseResponse.success(this.packageMapper.toDto(packageEntity));
    }

    @Override
    @Transactional
    public BaseResponse<?> buyAvailablePack(String packId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        if (user.getPaymentMethod() == null || user.getPaymentMethod().isBlank()) {
            throw new BadRequestException(ResponseStatus.INVALID, "Setup payment method first");
        }
        PackageEntity packageEntity = this.packageRepo.findById(UUID.fromString(packId)).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "No Package Found"));
        Optional<UserPackageEntity> userPackageEntityOptional = this.userPackageRepo.findByUserAndPackageEntity(user, packageEntity);
        if (userPackageEntityOptional.isEmpty()) {
            userPackageEntityOptional = this.userPackageRepo.findByUserAndCountry(user, packageEntity.getCountry());
        }
        UserPackageEntity userPackage;
        if (userPackageEntityOptional.isPresent()) {
            userPackage = userPackageEntityOptional.get();
            userPackage.setPackageEntity(packageEntity);
            userPackage.setExpireDate(userPackage.getExpireDate().plusDays(packageEntity.getExpiryDays()));
            userPackage.setRemainingCredit(userPackage.getRemainingCredit() + packageEntity.getCreditAmount());
        } else {
            userPackage = UserPackageEntity
                    .builder()
                    .user(user)
                    .packageEntity(packageEntity)
                    .remainingCredit(packageEntity.getCreditAmount())
                    .expireDate(LocalDate.now().plusDays(packageEntity.getExpiryDays()))
                    .build();
        }
        if (this.paymentService.chargePayment(userPackage)) {
            throw new UnexpectedException("Payment Failed");
        }
        userPackage = this.userPackageRepo.save(userPackage);
        PackagePurchaseHistoryEntity history = PackagePurchaseHistoryEntity
                .builder()
                .userPackage(userPackage)
                .purchaseDate(LocalDate.now())
                .build();
        this.packagePurchaseHistoryRepo.save(history);
        try {
            this.scheduler.upsertPackageReminder(user.getId(), packageEntity.getPackageId(), userPackage.getExpireDate());
        } catch (SchedulerException e) {
            this.paymentService.reChargePayment(userPackage);
            throw new UnexpectedException("Fail to make schedule");
        }
        List<PackagePurchaseHistoryEntity> purchaseHistoryEntities = this.packagePurchaseHistoryRepo.findByUserPackageOrderByPurchaseDateDesc(userPackage);
        List<LocalDate> purchaseDateList = purchaseHistoryEntities.stream().map(PackagePurchaseHistoryEntity::getPurchaseDate).toList();
        return BaseResponse.success(new UserPackageResponse(this.userPackageMapper.toDto(userPackage), purchaseDateList));
    }

    @Override
    public BaseResponse<List<UserPackageDto>> getOwnedPacks(String packageName, String status, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, elementCount, Sort.by(sortDirection, sortBy));
        Page<UserPackageEntity> userPackagePage = this.userPackageRepo.findByUser(user, pageable);
        return this.commonUtility.convertFromPage(userPackagePage, this.userPackageMapper::toDtoList);
    }

    @Override
    public BaseResponse<?> getOwnedPackDetail(String userPackId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        UserPackageEntity userPackage = this.userPackageRepo.findByUserPackageIdAndUser(UUID.fromString(userPackId), user).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "No User Package Found"));
        List<PackagePurchaseHistoryEntity> purchaseHistoryEntities = this.packagePurchaseHistoryRepo.findByUserPackageOrderByPurchaseDateDesc(userPackage);
        List<LocalDate> purchaseDateList = purchaseHistoryEntities.stream().map(PackagePurchaseHistoryEntity::getPurchaseDate).toList();
        return BaseResponse.success(new UserPackageResponse(this.userPackageMapper.toDto(userPackage), purchaseDateList));
    }
}
