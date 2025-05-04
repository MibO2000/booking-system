package com.mibo2000.codigo.codetest.controller;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.business.book.IBook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class BookingController {
    private final IBook iBook;

    @GetMapping("/class/available")
    public ResponseEntity<BaseResponse<?>> getAvailableClasses(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String countryCode,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer elementCount,
            @RequestParam(defaultValue = "classDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.getAvailableClasses(className, countryCode, pageNumber, elementCount, sortBy, direction, request));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<BaseResponse<?>> getClassDetail(
            @PathVariable String classId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.getClassDetail(classId, request));
    }

    @PostMapping("/class/{classId}/book")
    public ResponseEntity<BaseResponse<?>> addToBookClass(
            @PathVariable String classId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.addToBookClass(classId, request));
    }

    @PostMapping("/class/{classId}/wait")
    public ResponseEntity<BaseResponse<?>> addClassToWaitList(
            @PathVariable String classId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.addClassToWaitList(classId, request));
    }

    @GetMapping("/book/get")
    public ResponseEntity<BaseResponse<?>> getBookedClasses(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String bookingStatus,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer elementCount,
            @RequestParam(defaultValue = "classDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.getBookedClasses(className, bookingStatus, pageNumber, elementCount, sortBy, direction, request));
    }

    @GetMapping("/book/{bookId}/get")
    public ResponseEntity<BaseResponse<?>> getDetailedBookedClass(
            @PathVariable String bookId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.getDetailedBookedClass(bookId, request));
    }

    @PatchMapping("/book/{bookId}/attend")
    public ResponseEntity<BaseResponse<?>> attendClass(
            @PathVariable String bookId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.attendClass(bookId, request));
    }

    @PatchMapping("/book/{bookId}/cancel")
    public ResponseEntity<BaseResponse<?>> cancelBooking(
            @PathVariable String bookId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.cancelBooking(bookId, request));
    }

    @GetMapping("/wait/get")
    public ResponseEntity<BaseResponse<?>> getWaitListClasses(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String refunded,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer elementCount,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.getWaitListClasses(className, refunded, pageNumber, elementCount, sortBy, direction, request));
    }

    @GetMapping("/wait/{waitId}/get")
    public ResponseEntity<BaseResponse<?>> getDetailedWaitListClass(
            @PathVariable String waitId,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iBook.getDetailedWaitListClass(waitId, request));
    }
}
