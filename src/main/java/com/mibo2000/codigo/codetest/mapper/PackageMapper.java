package com.mibo2000.codigo.codetest.mapper;

import com.mibo2000.codigo.codetest.modal.dto.pack.PackageResponse;
import com.mibo2000.codigo.codetest.modal.entity.PackageEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CountryMapper.class})
public interface PackageMapper {
    PackageResponse toDto(PackageEntity packageEntity);

    List<PackageResponse> toDtoList(List<PackageEntity> packageEntities);

}
