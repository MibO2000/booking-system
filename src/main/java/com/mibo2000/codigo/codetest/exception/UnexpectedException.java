package com.mibo2000.codigo.codetest.exception;

import com.mibo2000.codigo.codetest.ResponseStatus;

public class UnexpectedException extends CommonException {
    public UnexpectedException(String message) {
        super(ResponseStatus.INVALID, message);
    }
}
