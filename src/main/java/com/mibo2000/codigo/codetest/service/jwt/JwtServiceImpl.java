package com.mibo2000.codigo.codetest.service.jwt;

import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import com.mibo2000.codigo.codetest.modal.repo.UserRepo;
import com.mibo2000.codigo.codetest.type.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final UserRepo userRepo;
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.jwtExpiration}")
    private Long jwtExpiration;
    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails,
                                String userId,
                                String email) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("tokenType", TokenType.NORMAL_TOKEN);
        extraClaims.put("email", email);
        extraClaims.put("userId", userId);
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails, String userId, String email) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("tokenType", TokenType.REFRESH_TOKEN);
        extraClaims.put("email", email);
        extraClaims.put("userId", userId);
        return buildToken(extraClaims, userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration
    ) {
        userRepo.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found!"));
        extraClaims.put("role", Collections.singletonList("USER"));
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }


    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public <T> T extractClaim(String token, String claimKey, Class<T> claimType) {
        final Claims claims = extractAllClaims(token);
        Object claimValue = claims.get(claimKey);
        if (claimType.isEnum()) {
            return claimType.cast(Enum.valueOf((Class<Enum>) claimType, claimValue.toString()));
        }
        return claimType.cast(claimValue);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
