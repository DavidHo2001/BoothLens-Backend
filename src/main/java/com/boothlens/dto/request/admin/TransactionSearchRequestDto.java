package com.boothlens.dto.request.admin;

import com.boothlens.dto.request.search.BaseSearchRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionSearchRequestDto extends BaseSearchRequestDto {
    private Integer userId;
    private String status;
    private String transactionType;
}
