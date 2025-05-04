package com.mibo2000.codigo.codetest.service.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface JwtService {
    String extractEmail(String token);

    String generateToken(UserDetails userDetails, String userId, String email);

    String generateRefreshToken(UserDetails userDetails, String userId, String email);

    boolean isTokenValid(String token, UserDetails userDetails);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    <T> T extractClaim(String token, String claimKey, Class<T> claimType);
}
