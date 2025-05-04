package com.mibo2000.codigo.codetest.business.user;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.ResponseStatus;
import com.mibo2000.codigo.codetest.business.BaseBusiness;
import com.mibo2000.codigo.codetest.exception.BadRequestException;
import com.mibo2000.codigo.codetest.mapper.UserMapper;
import com.mibo2000.codigo.codetest.modal.dto.user.ChangePasswordRequest;
import com.mibo2000.codigo.codetest.modal.dto.user.UserPaymentRequest;
import com.mibo2000.codigo.codetest.modal.dto.user.UserRequest;
import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import com.mibo2000.codigo.codetest.modal.repo.UserRepo;
import com.mibo2000.codigo.codetest.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserBusiness extends BaseBusiness implements IUser {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PaymentService paymentService;
    @Override
    public BaseResponse<?> getUserInfo(HttpServletRequest servletRequest) {
        UserEntity user = getUserEntity(servletRequest);
        return BaseResponse.success(this.userMapper.toDto(user));
    }

    @Override
    public BaseResponse<?> updateUserInfo(UserRequest request, HttpServletRequest servletRequest) {
        UserEntity user = getUserEntity(servletRequest);
        if (!user.getEmail().equals(request.email())){
            if (this.userRepo.existsByEmail(request.email())) {
                throw new BadRequestException(ResponseStatus.ACCOUNT_EXIST, "Email already exists");
            }
        }
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        this.userRepo.save(user);
        return BaseResponse.success(this.userMapper.toDto(user));
    }

    @Override
    public BaseResponse<?> updateUserPaymentInfo(UserPaymentRequest request, HttpServletRequest servletRequest) {
        UserEntity user = getUserEntity(servletRequest);
        if (this.paymentService.isInvalidPaymentRequest(request)) {
            throw new BadRequestException(ResponseStatus.INVALID, "Invalid Payment method");
        }
        user.setPaymentMethod(request.paymentMethod());
        user.setPaymentInfo(request.paymentInfo());
        this.userRepo.save(user);
        return BaseResponse.success(this.userMapper.toDto(user));
    }

    @Override
    public BaseResponse<?> changePassword(ChangePasswordRequest request, HttpServletRequest servletRequest) {
        UserEntity user = getUserEntity(servletRequest);
        if (!this.passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadRequestException(ResponseStatus.BAD_REQUEST, "Wrong current password");
        }
        user.setPassword(this.passwordEncoder.encode(request.newPassword()));
        this.userRepo.save(user);
        return BaseResponse.success(this.userMapper.toDto(user));
    }
}
