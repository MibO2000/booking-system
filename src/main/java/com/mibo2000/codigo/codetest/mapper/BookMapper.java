package com.mibo2000.codigo.codetest.mapper;

import com.mibo2000.codigo.codetest.modal.dto.book.BookingResponse;
import com.mibo2000.codigo.codetest.modal.entity.BookingInfoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CountryMapper.class, ClassMapper.class})
public interface BookMapper {
    BookingResponse toDto(BookingInfoEntity entity);
    List<BookingResponse> toDtoList(List<BookingInfoEntity> entityList);
}
