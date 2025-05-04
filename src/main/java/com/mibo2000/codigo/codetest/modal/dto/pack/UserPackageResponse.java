package com.mibo2000.codigo.codetest.modal.dto.pack;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserPackageResponse extends UserPackageDto{
    List<LocalDate> purchaseDateList;

    public UserPackageResponse(UserPackageDto dto, List<LocalDate> purchaseDateList) {
        super(dto.getUserPackageId(), dto.getPackageEntity(), dto.getRemainingCredit(), dto.getExpireDate());
        this.purchaseDateList = purchaseDateList;
    }

    public UserPackageResponse(String userPackageId, PackageResponse packageEntity, int remainingCredit, LocalDate expireDate) {
        super(userPackageId, packageEntity, remainingCredit, expireDate);
    }
}
