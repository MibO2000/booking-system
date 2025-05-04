package com.mibo2000.codigo.codetest.business.user;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.modal.dto.user.ChangePasswordRequest;
import com.mibo2000.codigo.codetest.modal.dto.user.UserPaymentRequest;
import com.mibo2000.codigo.codetest.modal.dto.user.UserRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface IUser {
    BaseResponse<?> getUserInfo(HttpServletRequest servletRequest);

    BaseResponse<?> updateUserInfo(UserRequest request, HttpServletRequest servletRequest);

    BaseResponse<?> updateUserPaymentInfo(UserPaymentRequest request, HttpServletRequest servletRequest);

    BaseResponse<?> changePassword(ChangePasswordRequest request, HttpServletRequest servletRequest);
}
