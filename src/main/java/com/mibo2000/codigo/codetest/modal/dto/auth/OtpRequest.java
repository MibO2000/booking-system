package com.mibo2000.codigo.codetest.modal.dto.auth;

import jakarta.validation.constraints.Email;

public record OtpRequest(
        @Email String email
) {
}
