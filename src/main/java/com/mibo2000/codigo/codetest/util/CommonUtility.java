package com.mibo2000.codigo.codetest.util;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.ResponseStatus;
import com.mibo2000.codigo.codetest.type.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonUtility {
    @Value("${academic-year.start-month:8}")
    private int ACADEMIC_YEAR_START_MONTH;

    public <T, D> BaseResponse<D> convertFromEntity(T entity, Function<T, D> converter) {
        if (entity == null) {
            return BaseResponse.statusError(ResponseStatus.NOT_FOUND, "Entity not found");
        }
        return BaseResponse.success(converter.apply(entity));
    }

    public <T, D> BaseResponse<List<D>> convertFromList(List<T> entities, Function<List<T>, List<D>> converter) {
        if (entities == null) {
            return BaseResponse.success(Collections.emptyList());
        }
        return BaseResponse.success(converter.apply(entities));
    }

    public BaseResponse<Void> createVoidResponse(ResponseStatus status) {
        return BaseResponse.status(status);
    }

    public <T, D> BaseResponse<List<D>> convertFromPage(Page<T> page, Function<List<T>, List<D>> converter) {
        if (page == null) {
            return BaseResponse.success(Collections.emptyList(), createEmptyPagination());
        }

        Pagination pagination = Pagination.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

        return BaseResponse.success(converter.apply(page.getContent()), pagination);
    }

    private Pagination createEmptyPagination() {
        return Pagination.builder()
                .page(0)
                .size(0)
                .totalElements(0)
                .totalPages(0)
                .last(true)
                .build();
    }
}