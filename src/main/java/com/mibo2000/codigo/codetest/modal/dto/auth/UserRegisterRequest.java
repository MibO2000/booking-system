package com.mibo2000.codigo.codetest.modal.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserRegisterRequest (
        @NotNull @NotBlank String username,
        @NotNull @NotBlank String fullName,
        @Email String email,
        @NotNull @NotBlank @Length(min = 4) String password
) {
}
