package com.mibo2000.codigo.codetest.modal.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record ResetPasswordRequest(
        @Email String email,
        @NotNull @NotBlank @Length(min = 6, max = 6) String otp,
        @NotNull @NotBlank @Length(min = 4) String newPassword
) {
}
