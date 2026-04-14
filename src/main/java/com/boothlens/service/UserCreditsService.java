package com.boothlens.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boothlens.entity.UserCredits;
import com.boothlens.repository.UserCreditsRepository;
import com.boothlens.constant.SystemConstants;
import com.boothlens.util.ValidationUtils;
import com.boothlens.mapper.UserMapper;
import com.boothlens.dto.response.UserCreditsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;

@Service
public class UserCreditsService {
    
    @Autowired
    private UserCreditsRepository userCreditsRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TransactionService transactionService;

    public UserCreditsResponse getUserCredits(Integer userId) {
        UserCredits userCredits = userCreditsRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(SystemConstants.ERR_USER_CREDITS_NOT_FOUND));
        return userMapper.toUserCreditsResponse(userCredits);
    }

    /**
     * 消耗 Token 進行 AI 分析
     * 🌟 加上 @Transactional 確保扣款與記帳同時成功或同時失敗
     * 🌟 加上 referenceCode 參數以便記帳
     */
    @Transactional
    public void consumeToken(Integer userId, Integer token, Integer superToken, String referenceCode) {
        UserCredits userCredits = userCreditsRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(SystemConstants.ERR_USER_CREDITS_NOT_FOUND));
        
        // 檢查餘額是否足夠
        ValidationUtils.validateTokenConsumption(userCredits, token, superToken);
        
        // 扣除 Token
        if(token > 0) userCredits.setToken(userCredits.getToken() - token);
        if(superToken > 0) userCredits.setSuperToken(userCredits.getSuperToken() - superToken);
        
        userCreditsRepository.save(userCredits);
        
        // 🌟 呼叫 TransactionService 寫入帳本
        transactionService.recordTokenConsumption(userId, token, superToken, referenceCode);
    }

    /**
     * 用戶入金 (增加 USD 餘額)
     */
    @Transactional
    public void addBalance(Integer userId, BigDecimal balance, String paymentGatewayRef) {
        UserCredits userCredits = userCreditsRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(SystemConstants.ERR_USER_CREDITS_NOT_FOUND));
        
        userCredits.setBalance(userCredits.getBalance().add(balance));
        userCreditsRepository.save(userCredits);

        // 🌟 寫入帳本
        transactionService.recordDeposit(userId, balance, paymentGatewayRef);
    }

    /**
     * 用戶購買/獲得 Token
     */
    @Transactional
    public void addToken(Integer userId, Integer token, BigDecimal usdCost, String orderRef) {
        UserCredits userCredits = userCreditsRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(SystemConstants.ERR_USER_CREDITS_NOT_FOUND));
        
        userCredits.setToken(userCredits.getToken() + token);
        
        // 如果是用餘額買的，這裡要扣除餘額
        if (usdCost != null && usdCost.compareTo(BigDecimal.ZERO) > 0) {
            userCredits.setBalance(userCredits.getBalance().subtract(usdCost));
        }
        
        userCreditsRepository.save(userCredits);

        // 🌟 寫入帳本
        transactionService.recordTokenPurchase(userId, usdCost != null ? usdCost : BigDecimal.ZERO, token, 0, orderRef);
    }

}
