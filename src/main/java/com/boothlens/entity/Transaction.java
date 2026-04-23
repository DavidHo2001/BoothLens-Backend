package com.boothlens.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "transaction_tb")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType; // DEPOSIT, PURCHASE_TOKEN, CONSUME_TOKEN, REFUND

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO; // USD

    @Column(name = "token_amount", nullable = false)
    private Integer tokenAmount = 0;

    @Column(name = "super_token_amount", nullable = false)
    private Integer superTokenAmount = 0;

    @Column(name = "reference_code", length = 100)
    private String referenceCode;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "SUCCESS";

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
}
