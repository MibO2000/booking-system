package com.mibo2000.codigo.codetest.modal.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotNull @NotBlank String fullName,
        @Email String email
) {
}
