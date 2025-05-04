package com.mibo2000.codigo.codetest.modal.dto.pub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryDto {
    private String countryName;
    private String countryCode;
    private String currency;
}
