package com.mibo2000.codigo.codetest.business.auth;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.ResponseStatus;
import com.mibo2000.codigo.codetest.business.BaseBusiness;
import com.mibo2000.codigo.codetest.exception.BadRequestException;
import com.mibo2000.codigo.codetest.exception.UnauthorizedException;
import com.mibo2000.codigo.codetest.exception.UnexpectedException;
import com.mibo2000.codigo.codetest.modal.dto.auth.*;
import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import com.mibo2000.codigo.codetest.modal.repo.UserRepo;
import com.mibo2000.codigo.codetest.service.jwt.JwtService;
import com.mibo2000.codigo.codetest.service.mail.MailService;
import com.mibo2000.codigo.codetest.service.redis.RedisService;
import com.mibo2000.codigo.codetest.type.TokenType;
import com.mibo2000.codigo.codetest.util.MessageConstants;
import com.mibo2000.codigo.codetest.util.Translator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthBusiness extends BaseBusiness implements IAuth {
    private final UserRepo userRepo;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    @Value("${jwt.jwtExpiration}")
    private Long jwtExpiration;

    @Override
    @Transactional
    public BaseResponse<?> registerNewUser(UserRegisterRequest request) {
        List<String> errorList = checkingSignUp(request);
        if (!errorList.isEmpty()) {
            String error = "";
            if (errorList.size() > 1) {
                error += ("Same " + String.join(", ", errorList) + "have already exists");
            } else {
                error += ("Same " + errorList.getFirst() + "has already exists");
            }
            return BaseResponse.error(error);
        }
        UserEntity user = UserEntity
                .builder()
                .email(request.email())
                .fullName(request.fullName())
                .username(request.username())
                .password(this.passwordEncoder.encode(request.password()))
                .verified(false)
                .verificationCode(UUID.randomUUID().toString())
                .build();
        this.userRepo.save(user);
        String url = String.format("http://localhost:8080/api/v1/auth/register/confirm?code=%s", user.getVerificationCode());
        if (!this.mailService.sendVerificationCode(request.email(), url)) {
            throw new UnexpectedException("Fail to send mail");
//            return BaseResponse.statusError(ResponseStatus.FAIL, );
        }
        return BaseResponse.success(url, "Please Verify via email");
    }

    private List<String> checkingSignUp(UserRegisterRequest request) {
        List<String> errorList = new ArrayList<>();
        if (this.userRepo.existsByEmail(request.email())) {
            errorList.add("Username");
        }
        if (this.userRepo.existsByUsername(request.username())) {
            errorList.add("Email");
        }
        return errorList;
    }

    @Override
    public BaseResponse<?> registerNewUserConfirmation(String code) {
        UserEntity user = this.userRepo.findByVerificationCode(code)
                .orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "Invalid Code"));
        user.setVerificationCode(null);
        user.setVerified(true);
        this.userRepo.save(user);
        return BaseResponse.success();
    }

    @Override
    public BaseResponse<?> loginUser(UserLoginRequest request) {
        UserEntity user = this.userRepo.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "Invalid Code"));
        if (!this.passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(Translator.toLocale(MessageConstants.UNAUTHORIZED_ERROR));
        }
        if (!user.getVerified()) {
            String url = String.format("http://localhost:8080/api/v1/auth/register/confirm?code=%s", user.getVerificationCode());
            if (!this.mailService.sendVerificationCode(user.getEmail(), url)) {
                throw new UnexpectedException("Fail to send mail");
//                return BaseResponse.statusError(ResponseStatus.FAIL, "Fail to send mail");
            }
            throw new UnauthorizedException("Please Verify account via email");
//            return BaseResponse.statusError(ResponseStatus.UN_AUTHORIZE, url, "Please Verify account via email");
        }
        return getAuthResponse(user, String.valueOf(user.getId()), user.getEmail());
    }

    @Override
    public BaseResponse<AuthResponse> refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(Translator.toLocale(MessageConstants.UNAUTHORIZED_ERROR));
        }
        refreshToken = authHeader.substring(7);

        username = this.jwtService.extractClaim(refreshToken, Claims::getSubject);
        TokenType tokenType = this.jwtService.extractClaim(refreshToken, "tokenType", TokenType.class);
        if (username != null && tokenType.equals(TokenType.REFRESH_TOKEN)) {
            UserEntity account = this.userRepo.findByUsername(username)
                    .orElseThrow(() -> new BadRequestException(ResponseStatus.UNAUTHORIZED, "Unauthorized."));
            if (!account.getVerified()) {
                throw new BadRequestException(ResponseStatus.UNAUTHORIZED, "The account is not yet verified.");
            }
            if (this.jwtService.isTokenValid(refreshToken, account)) {
                return getAuthResponse(account, account.getId().toString(), account.getEmail());
            }
        }
        throw new BadRequestException(ResponseStatus.UNAUTHORIZED, "Unauthorized.");
    }

    @Override
    public BaseResponse<?> resetPasswordOtp(OtpRequest request) {
        UserEntity account = this.userRepo.findByUsername(request.email())
                .orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "User email not found"));
        String otp = RandomStringUtils.randomNumeric(6);
        this.redisService.saveOtpAndInitRetry(account.getId().toString(), otp, 300);
        if (!this.mailService.sendOTPResetPasswordMail(otp, request.email(), "Reset Password")) {
            throw new UnexpectedException("Fail to send mail");
//            return BaseResponse.statusError(ResponseStatus.FAIL, "Fail to send mail");
        }
        return BaseResponse.success(otp, "OTP has sent to your email");
    }

    @Override
    public BaseResponse<?> resetPasswordConfirmed(ResetPasswordRequest request) {
        UserEntity account = this.userRepo.findByUsername(request.email())
                .orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "User email not found"));
        if (this.redisService.isOtpNotStored(account.getId().toString())) {
            throw new BadRequestException(ResponseStatus.INVALID, "OTP not found");
//            return BaseResponse.statusError(ResponseStatus.INVALID, "OTP not found");
        }
        if (this.redisService.incrementRetryAndCheck(account.getId().toString(), request.otp(), 5)) {
            String otp = RandomStringUtils.randomNumeric(6);
            this.redisService.saveOtpAndInitRetry(account.getId().toString(), otp, 300);
            if (!this.mailService.sendOTPResetPasswordMail(otp, request.email(), "Reset Password")) {
                throw new UnexpectedException("Fail to send mail");
//                return BaseResponse.statusError(ResponseStatus.FAIL, "Fail to send mail");
            }
            throw new BadRequestException(ResponseStatus.INVALID, "Wrong OTP, New OTP has send to email");
//            return BaseResponse.statusError(ResponseStatus.INVALID, "Wrong OTP, New OTP has send to email");
        }
        account.setPassword(this.passwordEncoder.encode(request.newPassword()));
        this.userRepo.save(account);
        return BaseResponse.success(null);
    }


    private BaseResponse<AuthResponse> getAuthResponse(UserDetails account, String userId, String email) {
        String jwtToken = this.jwtService.generateToken(account, userId, email);
        String refreshToken = this.jwtService.generateRefreshToken(account, userId, email);
        Date expiredAt = new Date(System.currentTimeMillis() + this.jwtExpiration);
        return BaseResponse.success(AuthResponse.of(jwtToken, expiredAt, refreshToken));
    }
}
