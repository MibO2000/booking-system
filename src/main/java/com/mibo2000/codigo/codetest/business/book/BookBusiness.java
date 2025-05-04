package com.mibo2000.codigo.codetest.business.book;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.ResponseStatus;
import com.mibo2000.codigo.codetest.business.BaseBusiness;
import com.mibo2000.codigo.codetest.exception.BadRequestException;
import com.mibo2000.codigo.codetest.exception.UnexpectedException;
import com.mibo2000.codigo.codetest.mapper.BookMapper;
import com.mibo2000.codigo.codetest.mapper.WaitListMapper;
import com.mibo2000.codigo.codetest.modal.EnumPool;
import com.mibo2000.codigo.codetest.modal.dto.book.ClassAvailResponse;
import com.mibo2000.codigo.codetest.modal.entity.*;
import com.mibo2000.codigo.codetest.modal.projection.ClassAvailabilityProjection;
import com.mibo2000.codigo.codetest.modal.repo.BookingInfoRepo;
import com.mibo2000.codigo.codetest.modal.repo.ClassRepo;
import com.mibo2000.codigo.codetest.modal.repo.UserPackageRepo;
import com.mibo2000.codigo.codetest.modal.repo.WaitingListRepo;
import com.mibo2000.codigo.codetest.service.redis.RedisService;
import com.mibo2000.codigo.codetest.service.schedule.PromoteUserSchedule;
import com.mibo2000.codigo.codetest.type.Pagination;
import com.mibo2000.codigo.codetest.util.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookBusiness extends BaseBusiness implements IBook {
    private final BookingInfoRepo bookingInfoRepo;
    private final ClassRepo classRepo;
    private final UserPackageRepo userPackageRepo;
    private final PromoteUserSchedule promoteUserSchedule;
    private final CommonUtility commonUtility;
    private final BookMapper bookMapper;
    private final WaitListMapper waitListMapper;
    private final WaitingListRepo waitingListRepo;
    private final RedisService redisService;

    @Override
    public BaseResponse<?> getAvailableClasses(String className, String countryCode, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, elementCount, Sort.by(sortDirection, sortBy));
        Page<ClassAvailabilityProjection> page = this.classRepo.findClassAvailabilityForUser(user.getId(), className, countryCode, pageable);
        Pagination pagination = Pagination.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
        return BaseResponse.success(page.getContent().stream().map(ClassAvailResponse::new).toList(), pagination);
    }

    @Override
    public BaseResponse<?> getClassDetail(String classId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        ClassAvailResponse response = new ClassAvailResponse(this.classRepo.findClassAvailabilityForClassId(UUID.fromString(classId)));
        response.setIsTimeSlotFreeToBook(response.isBooked() || response.isInWaitingList() || !this.bookingInfoRepo.existsBookingConflict(user.getId(), response.getClassDate(), response.getStartTime(), response.getEndTime()));
        response.setIsTimeSlotFreeToWaitList(!response.isBooked() && !response.isInWaitingList());
        return BaseResponse.success(response);
    }

    @Override
    @Transactional
    public BaseResponse<?> addToBookClass(String classId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        ClassEntity classEntity = this.classRepo.findById(UUID.fromString(classId)).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "Class not found"));
        UserPackageEntity userPackage = this.userPackageRepo.findByUserAndCountry(user, classEntity.getCountry()).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "User Package not found for this region"));
        checkBookRequest(user, classEntity, userPackage);
        try {
            userPackage.setRemainingCredit(userPackage.getRemainingCredit() - classEntity.getRequireCredit());
            this.userPackageRepo.save(userPackage);
            BookingInfoEntity bookingInfo = BookingInfoEntity
                    .builder()
                    .classInfo(classEntity)
                    .bookedAt(LocalDateTime.now())
                    .userPackage(userPackage)
                    .user(user)
                    .bookingStatus(EnumPool.BookingStatus.BOOKED)
                    .build();
            this.bookingInfoRepo.save(bookingInfo);
        } catch (Exception e) {
            this.redisService.reAddCountInBooking(UUID.fromString(classId));
            throw new UnexpectedException("Fail to book");
        }
        return BaseResponse.success(null);
    }

    private void checkRequest(UserEntity user, ClassEntity classEntity, UserPackageEntity userPackage) {
        if (this.bookingInfoRepo.existsByUserAndClassInfo(user, classEntity)) {
            throw new BadRequestException(ResponseStatus.BAD_REQUEST, "User has already booked");
        }
        if (this.bookingInfoRepo.existsBookingConflict(user.getId(), classEntity.getClassDate(), classEntity.getStartTime(), classEntity.getEndTime())) {
            throw new BadRequestException(ResponseStatus.BAD_REQUEST, "Booking Conflict exists");
        }
        if (classEntity.getRequireCredit() > userPackage.getRemainingCredit()) {
            throw new BadRequestException(ResponseStatus.BAD_REQUEST, "Not Enough credit");
        }
    }

    private void checkBookRequest(UserEntity user, ClassEntity classEntity, UserPackageEntity userPackage) {
        checkRequest(user, classEntity, userPackage);
        if (!redisService.attemptBooking(classEntity.getClassId()) || this.bookingInfoRepo.countByClassInfo(classEntity) >= classEntity.getCapacity()) {
            throw new BadRequestException(ResponseStatus.INVALID, "Booking full");
        }
    }

    @Override
    public BaseResponse<?> addClassToWaitList(String classId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        ClassEntity classEntity = this.classRepo.findById(UUID.fromString(classId)).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "Class not found"));
        UserPackageEntity userPackage = this.userPackageRepo.findByUserAndCountry(user, classEntity.getCountry()).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "User Package not found for this region"));
        checkWaitListRequest(user, classEntity, userPackage);
        try {
            userPackage.setRemainingCredit(userPackage.getRemainingCredit() - classEntity.getRequireCredit());
            this.userPackageRepo.save(userPackage);
            WaitingListEntity waitingList = WaitingListEntity
                    .builder()
                    .classInfo(classEntity)
                    .joinedAt(LocalDateTime.now())
                    .userPackage(userPackage)
                    .user(user)
                    .refunded(false)
                    .build();
            this.waitingListRepo.save(waitingList);
        } catch (Exception e) {
            this.redisService.reAddCountInBooking(UUID.fromString(classId));
            throw new UnexpectedException("Fail to book");
        }
        return BaseResponse.success(null);
    }

    private void checkWaitListRequest(UserEntity user, ClassEntity classEntity, UserPackageEntity userPackage) {
        checkRequest(user, classEntity, userPackage);
        if (redisService.attemptBooking(classEntity.getClassId()) &&
                this.bookingInfoRepo.countByClassInfo(classEntity) < classEntity.getCapacity()) {
            throw new BadRequestException(ResponseStatus.INVALID, "Booking is still available. Wait List not needed.");
        }
    }

    @Override
    public BaseResponse<?> getBookedClasses(String className, String bookingStatus, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, elementCount, Sort.by(sortDirection, sortBy));
        Page<BookingInfoEntity> page = this.bookingInfoRepo.findByUser(user, pageable);
        return this.commonUtility.convertFromPage(page, this.bookMapper::toDtoList);
    }

    @Override
    public BaseResponse<?> getDetailedBookedClass(String bookId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        BookingInfoEntity bookingInfo = bookingInfoRepo.findByBookingIdAndUser(UUID.fromString(bookId), user).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "Booking not found"));
        return BaseResponse.success(this.bookMapper.toDto(bookingInfo));
    }

    @Override
    public BaseResponse<?> attendClass(String bookId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        BookingInfoEntity bookingInfo = bookingInfoRepo.findByBookingIdAndUser(UUID.fromString(bookId), user).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "Booking not found"));
        if (bookingInfo.getClassInfo().getClassDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(bookingInfo.getClassInfo().getStartTime()) && LocalTime.now().isBefore(bookingInfo.getClassInfo().getEndTime())) {
            bookingInfo.setBookingStatus(EnumPool.BookingStatus.ATTENDED);
            this.bookingInfoRepo.save(bookingInfo);
        } else {
            throw new BadRequestException(ResponseStatus.INVALID, "The Class is not started or the class has already ended");
        }
        return BaseResponse.success(this.bookMapper.toDto(bookingInfo));
    }

    @Override
    @Transactional
    public BaseResponse<?> cancelBooking(String bookId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        BookingInfoEntity bookingInfo = bookingInfoRepo.findByBookingIdAndUser(UUID.fromString(bookId), user).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "Booking not found"));
        boolean refund;
        ClassEntity classEntity = bookingInfo.getClassInfo();
        if (LocalDate.now().isBefore(classEntity.getClassDate())) {
            refund = true;
        } else if (LocalDate.now().isEqual(classEntity.getClassDate())){
            LocalTime current = LocalTime.now();
            LocalTime startTime = classEntity.getStartTime();
            if (current.isBefore(startTime)) {
                refund = Duration.between(current, startTime).toHours() >= 4;
            } else {
                throw new BadRequestException(ResponseStatus.INVALID, "The class is now open, cannot be canceled");
            }
        } else {
            throw new BadRequestException(ResponseStatus.INVALID, "The class has ended");
        }
        if (refund) {
            UserPackageEntity userPackage = bookingInfo.getUserPackage();
            userPackage.setRemainingCredit(userPackage.getRemainingCredit() + classEntity.getRequireCredit());
            this.userPackageRepo.save(userPackage);
        }
        boolean hasWaitingUser = waitingListRepo.existsByClassInfoAndRefunded(classEntity, false);
        if (hasWaitingUser) {
            this.promoteUserSchedule.schedulePromoteUserJob(classEntity.getClassId());
        } else {
            this.redisService.reAddCountInBooking(classEntity.getClassId());
        }
        return BaseResponse.success(this.bookMapper.toDto(bookingInfo));
    }

    @Override
    public BaseResponse<?> getWaitListClasses(String className, String refunded, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, elementCount, Sort.by(sortDirection, sortBy));
        Page<WaitingListEntity> page = waitingListRepo.findByUser(user, pageable);
        return this.commonUtility.convertFromPage(page, this.waitListMapper::toDtoList);
    }

    @Override
    public BaseResponse<?> getDetailedWaitListClass(String waitId, HttpServletRequest request) {
        UserEntity user = getUserEntity(request);
        WaitingListEntity waitingList = waitingListRepo.findByWaitingListIdAndUser(UUID.fromString(waitId), user).orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "Wait List not found"));
        return BaseResponse.success(this.waitListMapper.toDto(waitingList));
    }
}
