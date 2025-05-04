package com.mibo2000.codigo.codetest.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.util.MessageConstants;
import com.mibo2000.codigo.codetest.util.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.crypto.BadPaddingException;
import java.util.HashMap;
import java.util.Map;

import static com.mibo2000.codigo.codetest.ResponseStatus.INVALID;
import static com.mibo2000.codigo.codetest.ResponseStatus.UNKNOWN;

@RestControllerAdvice
@Slf4j
public class APIExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> businessException(BadRequestException ex) {
        log.error("Error ", ex);
        return BaseResponse.statusError(ex.getResponseStatus(), ex.getMessage());
    }

    @ExceptionHandler(UnexpectedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse<String> unexpectedException(UnexpectedException ex) {
        log.error("Unexpected Error ", ex);
        return BaseResponse.statusError(UNKNOWN, ex.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            BadPaddingException.class,
            JsonProcessingException.class,
            HttpMessageNotReadableException.class,
            IndexOutOfBoundsException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse<Map<String, String>> validateException(Exception ex) {
        log.error("Error ", ex);
        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult result = ((MethodArgumentNotValidException) ex).getBindingResult();
            Map<String, String> errList = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errList.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return BaseResponse.statusErrorWithData(INVALID, "Invalid Request", errList);
        }
        return BaseResponse.statusError(INVALID, Translator.toLocale(MessageConstants.BAD_REQUEST_ERROR));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Object> handleAllException(Exception ex) {
        log.error("Error ", ex);
        return BaseResponse.statusError(UNKNOWN, Translator.toLocale(MessageConstants.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public BaseResponse<Object> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Error ", ex);
        return BaseResponse.statusError(ex.getResponseStatus(), Translator.toLocale(MessageConstants.UNAUTHORIZED_ERROR));
    }
}
