package com.boothlens.util;

import com.boothlens.constant.SystemConstants;
import com.boothlens.entity.UserCredits;

public class ValidationUtils {
    
    public static void validatePasswordMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException(SystemConstants.ERR_PASSWORD_MISMATCH);
        }
    }
    
    public static void validatePasswordStrength(String password, int minLength) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (password.length() < minLength) {
            throw new IllegalArgumentException("Password must be at least " + minLength + " characters");
        }
    }
    
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    public static void validateTokenConsumption(UserCredits userCredits, Integer token, Integer superToken) {
        if (token == null) token = 0;
        if (superToken == null) superToken = 0;
        if (token < 0 || superToken < 0 || (token==0 && superToken==0)) {
            throw new IllegalArgumentException(SystemConstants.GENERIC_INPUT_ERROR);
        }
        if (userCredits.getToken() < token || userCredits.getSuperToken() < superToken) {
            throw new IllegalArgumentException(SystemConstants.ERR_INSUFFICIENT_CREDITS);
        }
    }
    
    private ValidationUtils() {
    }
}
