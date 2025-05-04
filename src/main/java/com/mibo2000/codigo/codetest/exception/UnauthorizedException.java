package com.mibo2000.codigo.codetest.exception;

import com.mibo2000.codigo.codetest.ResponseStatus;

public class UnauthorizedException extends CommonException {
    public UnauthorizedException(String message) {
        super(ResponseStatus.UN_AUTHORIZE, message);
    }
}
