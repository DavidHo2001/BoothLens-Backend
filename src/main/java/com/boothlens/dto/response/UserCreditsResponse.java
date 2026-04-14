package com.boothlens.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditsResponse {

    private Integer userId;
    private BigDecimal balance;
    private Integer token;
    private Integer superToken;
}
