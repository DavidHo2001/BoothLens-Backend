package com.boothlens.dto.request.admin;

import com.boothlens.dto.request.search.BaseSearchRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCreditsSearchRequestDto extends BaseSearchRequestDto {
    private Integer userId;

    @Override
    public Pageable getPageable() {
        Sort.Direction direction = Sort.Direction.fromString(getSortDir());
        //default is CreatedAt, but user credit does not have this field
        Sort sort = Sort.by(direction, "userId");
        return PageRequest.of(getPage() - 1, getLimit(), sort);
    }
}
