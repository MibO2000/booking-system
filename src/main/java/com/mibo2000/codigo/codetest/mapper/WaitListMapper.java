package com.mibo2000.codigo.codetest.mapper;

import com.mibo2000.codigo.codetest.modal.dto.book.WaitListResponse;
import com.mibo2000.codigo.codetest.modal.entity.WaitingListEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CountryMapper.class, ClassMapper.class})
public interface WaitListMapper {
    WaitListResponse toDto(WaitingListEntity entity);
    List<WaitListResponse> toDtoList(List<WaitingListEntity> entityList);

}
