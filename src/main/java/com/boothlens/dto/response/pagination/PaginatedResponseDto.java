package com.boothlens.dto.response.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDto<T> {

    private List<T> data;
    private PaginationMeta pagination;

    // Helper constructor to easily map from Spring Data's Page<T> object
    public PaginatedResponseDto(Page<T> pageResult) {
        this.data = pageResult.getContent();
        this.pagination = PaginationMeta.builder()
                // Spring Data pages are 0-indexed, but APIs usually expose 1-indexed pages
                .page(pageResult.getNumber() + 1) 
                .limit(pageResult.getSize())
                .total(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMeta {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }
}
