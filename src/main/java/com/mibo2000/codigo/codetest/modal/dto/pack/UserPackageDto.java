package com.mibo2000.codigo.codetest.modal.dto.pack;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserPackageDto {
    private String userPackageId;
    private PackageResponse packageEntity;
    private int remainingCredit;
    private LocalDate expireDate;
}
