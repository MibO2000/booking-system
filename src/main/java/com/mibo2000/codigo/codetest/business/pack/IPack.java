package com.mibo2000.codigo.codetest.business.pack;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.modal.dto.pack.PackageResponse;
import com.mibo2000.codigo.codetest.modal.dto.pack.UserPackageDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IPack {
    BaseResponse<List<PackageResponse>> getAvailablePacks(String packageName, String countryCode, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request);

    BaseResponse<?> getAvailablePackDetail(String packId, HttpServletRequest request);

    BaseResponse<?> buyAvailablePack(String packId, HttpServletRequest request);

    BaseResponse<List<UserPackageDto>> getOwnedPacks(String packageName, String status, Integer pageNumber, Integer elementCount, String sortBy, String direction, HttpServletRequest request);

    BaseResponse<?> getOwnedPackDetail(String userPackId, HttpServletRequest request);
}
