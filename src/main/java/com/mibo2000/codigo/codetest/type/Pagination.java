package com.mibo2000.codigo.codetest.type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private int totalElements = 0;

    @Builder.Default
    private int totalPages = 0;

    @Builder.Default
    private boolean last = true;

    public void setPage(int page) {
        this.page = Math.max(0, page);
    }

    public void setSize(int size) {
        this.size = Math.max(1, Math.min(100, size));
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = Math.max(0, totalElements);
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = Math.max(0, totalPages);
    }
}
