package com.boothlens.dto.request.admin;

import com.boothlens.dto.request.search.BaseSearchRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRequestSearchRequestDto extends BaseSearchRequestDto {
    private String status;
}
