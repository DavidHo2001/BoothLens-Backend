package com.boothlens.dto.request.search;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class BaseSearchRequestDto {

    private String keyword;

    @Min(value = 1, message = "Page number must be at least 1")
    private int page = 1; // Default to page 1

    @Min(value = 1, message = "Limit must be at least 1")
    private int limit = 10; // Default limit

    private String sortBy = "createdAt"; // Default sort column
    private String sortDir = "DESC"; // Default sort direction

    // Helper method to convert this DTO into a Spring Data Pageable object
    public Pageable getPageable() {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(direction, sortBy);
        
        // Convert 1-indexed API page back to 0-indexed Spring Data page
        return PageRequest.of(page - 1, limit, sort);
    }
}
