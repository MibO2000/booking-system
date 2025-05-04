package com.mibo2000.codigo.codetest;

import com.mibo2000.codigo.codetest.type.Pagination;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
@NoArgsConstructor
public class BaseResponse<T> {
    private ResponseStatus status;
    private T data;
    private Pagination pagination;
    private String message;

    public static <T> BaseResponse<T> as(ResponseStatus status, T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(status);
        response.setData(data);
        return response;
    }

    public static <T> BaseResponse<T> as(ResponseStatus status, T data, String message) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(status);
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    public static <T> BaseResponse<T> as(ResponseStatus status, T data, Pagination pagination) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(status);
        response.setData(data);
        response.setPagination(pagination);
        return response;
    }

    public static <T> BaseResponse<T> success(T data) {
        return as(ResponseStatus.SUCCESS, data);
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return as(ResponseStatus.SUCCESS, data, message);
    }

    public static <T> BaseResponse<T> success(T data, Pagination pagination) {
        return as(ResponseStatus.SUCCESS, data, pagination);
    }

    public static <T> BaseResponse<T> status(ResponseStatus status) {
        return as(ResponseStatus.SUCCESS, null);
    }

    public static <T> BaseResponse<T> success() {
        return status(ResponseStatus.SUCCESS);
    }

    public static <T> BaseResponse<T> error(T data) {
        return as(ResponseStatus.ERROR, data);
    }

    public static <T> BaseResponse<T> statusError(ResponseStatus status, String errorMessage) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(status);
        response.setMessage(errorMessage);
        return response;
    }

    public static <T> BaseResponse<T> statusError(ResponseStatus status, T data, String errorMessage) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(status);
        response.setMessage(errorMessage);
        response.setData(data);
        return response;
    }

    public static BaseResponse<Map<String, String>> statusErrorWithData(ResponseStatus responseStatus, String invalidRequest, Map<String, String> errList) {
        BaseResponse<Map<String, String>> response = new BaseResponse<>();
        response.setStatus(responseStatus);
        response.setMessage(invalidRequest);
        response.setData(errList);
        return response;
    }
}
