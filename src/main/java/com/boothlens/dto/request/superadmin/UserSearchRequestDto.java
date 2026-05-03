package com.boothlens.dto.request.superadmin;

import com.boothlens.dto.request.search.BaseSearchRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserSearchRequestDto extends BaseSearchRequestDto {
    private Integer roleId;
    private Boolean isBlocked;
}
