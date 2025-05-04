package com.mibo2000.codigo.codetest.controller;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.business.user.IUser;
import com.mibo2000.codigo.codetest.modal.dto.user.ChangePasswordRequest;
import com.mibo2000.codigo.codetest.modal.dto.user.UserPaymentRequest;
import com.mibo2000.codigo.codetest.modal.dto.user.UserRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final IUser iUser;

    @GetMapping("/info")
    public ResponseEntity<BaseResponse<?>> getUserInfo(HttpServletRequest servletRequest) {
        return ResponseEntity.ok(this.iUser.getUserInfo(servletRequest));
    }

    @PatchMapping("/info/update")
    public ResponseEntity<BaseResponse<?>> updateUserInfo(@Validated @RequestBody UserRequest request,
                                                          HttpServletRequest servletRequest) {
        return ResponseEntity.ok(this.iUser.updateUserInfo(request, servletRequest));
    }

    @PatchMapping("/payment/update")
    public ResponseEntity<BaseResponse<?>> updateUserPaymentInfo(@Validated @RequestBody UserPaymentRequest request,
                                                                 HttpServletRequest servletRequest) {
        return ResponseEntity.ok(this.iUser.updateUserPaymentInfo(request, servletRequest));
    }

    @PostMapping("/password/change")
    public ResponseEntity<BaseResponse<?>> changePassword(@Validated @RequestBody ChangePasswordRequest request,
                                                          HttpServletRequest servletRequest) {
        return ResponseEntity.ok(this.iUser.changePassword(request, servletRequest));
    }
}
