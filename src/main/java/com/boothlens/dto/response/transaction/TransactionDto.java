package com.boothlens.dto.response.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Integer id;
    private Integer userId;
    private String transactionType;
    private BigDecimal amount;
    private Integer tokenAmount;
    private Integer superTokenAmount;
    private String referenceCode;
    private String description;
    private String status;
    private Instant createdAt;
}
