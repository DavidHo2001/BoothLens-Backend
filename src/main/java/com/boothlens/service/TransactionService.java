package com.boothlens.service;

import com.boothlens.dto.request.admin.TransactionSearchRequestDto;
import com.boothlens.dto.response.pagination.PaginatedResponseDto;
import com.boothlens.dto.response.transaction.TransactionDto;
import com.boothlens.entity.Transaction;
import com.boothlens.repository.TransactionRepository;
import com.boothlens.specification.TransactionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 最底層的通用建立方法
     */
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * 情境 1：用戶儲值 USD (入金)
     */
    @Transactional
    public void recordDeposit(Integer userId, BigDecimal usdAmount, String paymentGatewayRef) {
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setTransactionType("DEPOSIT");
        tx.setAmount(usdAmount); // 正數
        tx.setReferenceCode(paymentGatewayRef);
        tx.setDescription("User deposited USD via payment gateway");
        transactionRepository.save(tx);
    }

    /**
     * 情境 2：用戶花 USD 購買 Token
     */
    @Transactional
    public void recordTokenPurchase(Integer userId, BigDecimal usdCost, int tokensGained, int superTokensGained, String orderRef) {
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setTransactionType("PURCHASE_TOKEN");
        tx.setAmount(usdCost.negate()); // 負數，代表扣除餘額
        tx.setTokenAmount(tokensGained); // 正數，代表獲得 Token
        tx.setSuperTokenAmount(superTokensGained);
        tx.setReferenceCode(orderRef);
        tx.setDescription("Purchased tokens with account balance");
        transactionRepository.save(tx);
    }

    /**
     * 情境 3：用戶消耗 Token 進行 AI 分析 (Booth Analysis)
     */
    @Transactional
    public void recordTokenConsumption(Integer userId, int tokensUsed, int superTokensUsed, String analysisRefCode) {
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setTransactionType("CONSUME_TOKEN");
        tx.setTokenAmount(-tokensUsed); // 負數，代表消耗 Token
        tx.setSuperTokenAmount(-superTokensUsed);
        tx.setReferenceCode(analysisRefCode);
        tx.setDescription("Consumed tokens for Booth AI Analysis");
        transactionRepository.save(tx);
    }

    @Transactional
    public void recordRefund(Integer userId, BigDecimal usdAmount, int tokensRefunded, int superTokensRefunded, String referenceCode) {
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setTransactionType("REFUND");
        tx.setAmount(usdAmount);
        tx.setTokenAmount(tokensRefunded);
        tx.setSuperTokenAmount(superTokensRefunded);
        tx.setReferenceCode(referenceCode);
        tx.setDescription(getDescription(referenceCode));
        transactionRepository.save(tx);
        log.info("Recorded refund for user {} with reference code {}", userId, referenceCode);
    }

    public List<TransactionDto> getCurrentUserTransactions(Integer userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PaginatedResponseDto<TransactionDto> adminSearch(TransactionSearchRequestDto request) {
        Page<Transaction> page = transactionRepository.findAll(
                TransactionSpecifications.withFilters(
                        request.getKeyword(),
                        request.getUserId(),
                        request.getStatus(),
                        request.getTransactionType()
                ),
                request.getPageable()
        );
        return new PaginatedResponseDto<>(page.map(this::toDto));
    }

    @Transactional
    public Transaction createAdminAdjustment(Integer userId,
                                             BigDecimal balanceDelta,
                                             Integer tokenDelta,
                                             Integer superTokenDelta,
                                             String referenceCode,
                                             String description) {
        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setTransactionType("ADMIN_ADJUST");
        tx.setAmount(balanceDelta != null ? balanceDelta : BigDecimal.ZERO);
        tx.setTokenAmount(tokenDelta != null ? tokenDelta : 0);
        tx.setSuperTokenAmount(superTokenDelta != null ? superTokenDelta : 0);
        tx.setReferenceCode(referenceCode);
        tx.setDescription(description != null ? description : "Admin adjusted user credits");
        tx.setStatus("SUCCESS");
        return transactionRepository.saveAndFlush(tx);
    }

    private String getDescription(String referenceCode) {
        if (referenceCode == null || referenceCode.isBlank()) return "Refund for unknown reason";
        String codePrefix = referenceCode.split("-")[0];
        switch (codePrefix) {
            case "BA":
                return "Refund for Booth AI Analysis";
            default:
                return "Refund for unknown reason";
        }
    }

    private TransactionDto toDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .userId(transaction.getUserId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .tokenAmount(transaction.getTokenAmount())
                .superTokenAmount(transaction.getSuperTokenAmount())
                .referenceCode(transaction.getReferenceCode())
                .description(transaction.getDescription())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
