package com.mibo2000.codigo.codetest.modal.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record ChangePasswordRequest(
        @NotNull @NotBlank @Length(min = 4) String oldPassword,
        @NotNull @NotBlank @Length(min = 4) String newPassword
) {
}
