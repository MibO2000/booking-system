package com.mibo2000.codigo.codetest.modal.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPaymentRequest(
        @NotBlank @NotNull String paymentMethod,
        @NotBlank @NotNull String paymentInfo
) {
}
