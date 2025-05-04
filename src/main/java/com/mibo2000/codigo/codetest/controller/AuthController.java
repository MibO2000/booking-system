package com.mibo2000.codigo.codetest.controller;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.business.auth.IAuth;
import com.mibo2000.codigo.codetest.modal.dto.auth.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final IAuth iAuth;
    @PostMapping("/auth/register")
    public ResponseEntity<BaseResponse<?>> registerNewUser(@Validated @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(this.iAuth.registerNewUser(request));
    }

//    mail send with link so that, the link will help change the user verified
    @GetMapping("/auth/register/confirm")
    public ResponseEntity<BaseResponse<?>> registerNewUserConfirmation(@RequestParam String code) {
        return ResponseEntity.ok(this.iAuth.registerNewUserConfirmation(code));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<BaseResponse<?>> loginUser(@Validated @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(this.iAuth.loginUser(request));
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<BaseResponse<AuthResponse>> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(this.iAuth.refreshToken(request));
    }

    @PostMapping("/auth/reset/otp")
    public ResponseEntity<BaseResponse<?>> resetPasswordOtp(@Validated @RequestBody OtpRequest request) {
        return ResponseEntity.ok(this.iAuth.resetPasswordOtp(request));
    }

    @PostMapping("/auth/reset/confirm")
    public ResponseEntity<BaseResponse<?>> resetPasswordConfirmed(@Validated @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(this.iAuth.resetPasswordConfirmed(request));
    }
}
