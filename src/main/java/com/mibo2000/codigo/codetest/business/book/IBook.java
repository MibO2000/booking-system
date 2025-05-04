package com.mibo2000.codigo.codetest.business.book;

import com.mibo2000.codigo.codetest.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IBook {
    BaseResponse<?> getAvailableClasses(String className, String countryCode, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request);

    BaseResponse<?> getClassDetail(String classId, HttpServletRequest request);

    BaseResponse<?> addToBookClass(String classId, HttpServletRequest request);

    BaseResponse<?> addClassToWaitList(String classId, HttpServletRequest request);

    BaseResponse<?> getBookedClasses(String className, String bookingStatus, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request);

    BaseResponse<?> getDetailedBookedClass(String bookId, HttpServletRequest request);

    BaseResponse<?> attendClass(String bookId, HttpServletRequest request);

    BaseResponse<?> cancelBooking(String bookId, HttpServletRequest request);

    BaseResponse<?> getWaitListClasses(String className, String refunded, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request);

    BaseResponse<?> getDetailedWaitListClass(String waitId, HttpServletRequest request);
}
