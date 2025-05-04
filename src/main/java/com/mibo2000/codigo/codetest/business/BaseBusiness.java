package com.mibo2000.codigo.codetest.business;

import com.mibo2000.codigo.codetest.ResponseStatus;
import com.mibo2000.codigo.codetest.exception.BadRequestException;
import com.mibo2000.codigo.codetest.exception.UnauthorizedException;
import com.mibo2000.codigo.codetest.modal.dto.user.UserPaymentRequest;
import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;
import com.mibo2000.codigo.codetest.modal.repo.UserRepo;
import com.mibo2000.codigo.codetest.service.jwt.JwtService;
import com.mibo2000.codigo.codetest.util.MessageConstants;
import com.mibo2000.codigo.codetest.util.Translator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class BaseBusiness {
    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepo userRepo;

    protected String getUsername(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(Translator.toLocale(MessageConstants.UNAUTHORIZED_ERROR));
        }
        refreshToken = authHeader.substring(7);

        return jwtService.extractClaim(refreshToken, Claims::getSubject);
    }

    protected UserEntity getUserEntity(HttpServletRequest request) {
        UserEntity user = userRepo.findByUsername(getUsername(request))
                .orElseThrow(() -> new BadRequestException(ResponseStatus.NOT_FOUND, "User not found"));
        if (!user.getVerified()) {
            throw new UnauthorizedException(Translator.toLocale(MessageConstants.UNAUTHORIZED_ERROR));
        }
        return user;
    }
}
