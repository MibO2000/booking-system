package com.mibo2000.codigo.codetest.controller;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.business.pack.IPack;
import com.mibo2000.codigo.codetest.modal.dto.pack.PackageResponse;
import com.mibo2000.codigo.codetest.modal.dto.pack.UserPackageDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user/pack")
@RestController
@RequiredArgsConstructor
public class PackageController {
    final IPack iPack;

    @GetMapping("/available")
    public ResponseEntity<BaseResponse<List<PackageResponse>>> getAvailablePacks(
            @RequestParam(required = false) String packageName,
            @RequestParam(required = false) String countryCode,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer elementCount,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iPack.getAvailablePacks(packageName, countryCode, pageNumber, elementCount, sortBy, direction, request));
    }

    @GetMapping("/available/{packId}")
    public ResponseEntity<BaseResponse<?>> getAvailablePackDetail(@PathVariable String packId,
                                                                  HttpServletRequest request) {
        return ResponseEntity.ok(this.iPack.getAvailablePackDetail(packId, request));
    }

    // owned will be extended, add payment in here
    @PostMapping("/available/{packId}/purchase")
    public ResponseEntity<BaseResponse<?>> buyAvailablePack(@PathVariable String packId,
                                                            HttpServletRequest request) {
        return ResponseEntity.ok(this.iPack.buyAvailablePack(packId, request));
    }

    @GetMapping("/owned")
    public ResponseEntity<BaseResponse<List<UserPackageDto>>> getOwnedPacks(
            @RequestParam(required = false) String packageName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer elementCount,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.iPack.getOwnedPacks(packageName, status, pageNumber, elementCount, sortBy, direction, request));
    }

    @GetMapping("/owned/{userPackId}")
    public ResponseEntity<BaseResponse<?>> getOwnedPackDetail(@PathVariable String userPackId,
                                                              HttpServletRequest request) {
        return ResponseEntity.ok(this.iPack.getOwnedPackDetail(userPackId, request));
    }
}
