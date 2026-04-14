package com.boothlens.service;

import com.boothlens.entity.Transaction;
import com.boothlens.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
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
}
