package com.mibo2000.codigo.codetest.business.auth;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.modal.dto.auth.*;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuth {
    BaseResponse<?> registerNewUser(UserRegisterRequest request);

    BaseResponse<?> registerNewUserConfirmation(String code);

    BaseResponse<?> loginUser(UserLoginRequest request);

    BaseResponse<AuthResponse> refreshToken(HttpServletRequest request);

    BaseResponse<?> resetPasswordOtp(OtpRequest request);

    BaseResponse<?> resetPasswordConfirmed(ResetPasswordRequest request);

}
