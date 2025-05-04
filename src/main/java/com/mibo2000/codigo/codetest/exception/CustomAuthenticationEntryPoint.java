package com.mibo2000.codigo.codetest.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.ResponseStatus;
import com.mibo2000.codigo.codetest.util.MessageConstants;
import com.mibo2000.codigo.codetest.util.Translator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        BaseResponse<Object> customResponse = BaseResponse.statusError(ResponseStatus.UN_AUTHORIZE, Translator.toLocale(MessageConstants.ACCESS_DENIED_ERROR));
        String jsonResponse = objectMapper.writeValueAsString(customResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonResponse);
    }
}
