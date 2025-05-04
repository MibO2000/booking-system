package com.mibo2000.codigo.codetest.modal.dto.auth;

import java.util.Date;

public record AuthResponse(
        String token,
        Date expiredAt,
        String refreshToken
) {
    public static AuthResponse of(String token, Date expiredAt, String refreshToken) {
        return new AuthResponse(token, expiredAt, refreshToken);
    }
}

