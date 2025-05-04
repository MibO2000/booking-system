package com.mibo2000.codigo.codetest.mapper;

import com.mibo2000.codigo.codetest.modal.dto.book.ClassResponse;
import com.mibo2000.codigo.codetest.modal.entity.ClassEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CountryMapper.class})
public interface ClassMapper {
    ClassResponse toDto(ClassEntity classEntity);
    List<ClassResponse> toDtoList(List<ClassEntity> classEntityList);
}
