package com.boothlens.dto.request.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCreditsAdjustRequestDto {
    private Integer userId;
    private Integer tokenDelta = 0;
    private Integer superTokenDelta = 0;
    private BigDecimal balanceDelta = BigDecimal.ZERO;
    private String referenceCode;
    private String description;
}
