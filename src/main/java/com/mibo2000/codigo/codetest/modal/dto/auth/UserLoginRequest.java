package com.mibo2000.codigo.codetest.modal.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
        @NotNull @NotBlank String username,
        @NotNull @NotBlank String password
) {
}
