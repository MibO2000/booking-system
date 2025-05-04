package com.mibo2000.codigo.codetest.mapper;

import com.mibo2000.codigo.codetest.modal.dto.pack.UserPackageDto;
import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CountryMapper.class, PackageMapper.class})
public interface UserPackageMapper {
    UserPackageDto toDto(UserPackageEntity userPackageEntity);

    List<UserPackageDto> toDtoList(List<UserPackageEntity> userPackageEntities);
}
