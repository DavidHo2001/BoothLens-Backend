package com.boothlens.service;

import com.boothlens.dto.request.admin.UserCreditsAdjustRequestDto;
import com.boothlens.dto.request.admin.UserCreditsSearchRequestDto;
import com.boothlens.dto.response.pagination.PaginatedResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boothlens.entity.UserCredits;
import com.boothlens.entity.UserRequest;
import com.boothlens.repository.UserCreditsRepository;
import com.boothlens.specification.UserCreditsSpecifications;
import com.boothlens.constant.SystemConstants;
import com.boothlens.util.ValidationUtils;
import com.boothlens.mapper.UserMapper;
import com.boothlens.dto.response.UserCreditsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Consumed {} tokens and {} super tokens for user {} with reference code {}", token, superToken, userId, referenceCode);
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

    @Transactional
    public void refund(UserRequest savedUserRequest, BigDecimal usdAmount, Integer token, Integer superToken) {
        UserCredits userCredits = userCreditsRepository.findByUserId(savedUserRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(SystemConstants.ERR_USER_CREDITS_NOT_FOUND));
        
        userCredits.setToken(userCredits.getToken() + token);
        userCredits.setSuperToken(userCredits.getSuperToken() + superToken);

        UserCredits savedUserCredit = userCreditsRepository.saveAndFlush(userCredits);

        transactionService.recordRefund(savedUserCredit.getUserId(), usdAmount, token, superToken, savedUserRequest.getReferenceCode());
        log.info("Refunded {} USD, {} tokens and {} super tokens for user {} with reference code {}", 
        usdAmount, token, superToken, savedUserCredit.getUserId(), savedUserRequest.getReferenceCode());
    }

    public PaginatedResponseDto<UserCreditsResponse> adminSearch(UserCreditsSearchRequestDto request) {
        Page<UserCredits> page = userCreditsRepository.findAll(
                UserCreditsSpecifications.withFilters(request.getKeyword(), request.getUserId()),
                request.getPageable()
        );
        return new PaginatedResponseDto<>(page.map(userMapper::toUserCreditsResponse));
    }

    @Transactional
    public UserCreditsResponse adminAdjustCredits(UserCreditsAdjustRequestDto request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId is required");
        }

        UserCredits userCredits = userCreditsRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(SystemConstants.ERR_USER_CREDITS_NOT_FOUND));

        BigDecimal balanceDelta = request.getBalanceDelta() != null ? request.getBalanceDelta() : BigDecimal.ZERO;
        Integer tokenDelta = request.getTokenDelta() != null ? request.getTokenDelta() : 0;
        Integer superTokenDelta = request.getSuperTokenDelta() != null ? request.getSuperTokenDelta() : 0;

        // Apply delta values and block negative balances.
        BigDecimal newBalance = userCredits.getBalance().add(balanceDelta);
        int newToken = userCredits.getToken() + tokenDelta;
        int newSuperToken = userCredits.getSuperToken() + superTokenDelta;
        if (newBalance.compareTo(BigDecimal.ZERO) < 0 || newToken < 0 || newSuperToken < 0) {
            throw new IllegalArgumentException("Adjustment would result in negative credits");
        }

        userCredits.setBalance(newBalance);
        userCredits.setToken(newToken);
        userCredits.setSuperToken(newSuperToken);
        userCreditsRepository.saveAndFlush(userCredits);

        transactionService.createAdminAdjustment(
                request.getUserId(),
                balanceDelta,
                tokenDelta,
                superTokenDelta,
                request.getReferenceCode(),
                request.getDescription()
        );

        return userMapper.toUserCreditsResponse(userCredits);
    }
}
