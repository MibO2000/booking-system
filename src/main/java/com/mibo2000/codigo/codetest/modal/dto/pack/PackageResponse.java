package com.mibo2000.codigo.codetest.modal.dto.pack;

import com.mibo2000.codigo.codetest.modal.dto.pub.CountryDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PackageResponse {
    private UUID packageId;
    private CountryDto country;
    private String packageName;
    private int creditAmount;
    private BigDecimal price;
    private int expiryDays;
}
