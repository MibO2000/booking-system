package com.mibo2000.codigo.codetest.exception;


import com.mibo2000.codigo.codetest.ResponseStatus;

public class BadRequestException extends CommonException {
    public BadRequestException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
