package com.mibo2000.codigo.codetest.exception;

import com.mibo2000.codigo.codetest.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonException extends RuntimeException {
    private ResponseStatus responseStatus;
    private String message;
}
