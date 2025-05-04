package com.mibo2000.codigo.codetest.mapper;

import com.mibo2000.codigo.codetest.modal.dto.pub.CountryDto;
import com.mibo2000.codigo.codetest.modal.entity.CountryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryDto toDto(CountryEntity country);
    List<CountryDto> toDtoList(List<CountryEntity> countryList);
}