package com.boothlens.util;

import com.boothlens.dto.jwt.JwtProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 檢查當前請求是否有已登入的使用者
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 核心方法：直接從 SecurityContext 拿出 JwtProfile
     */
    public static JwtProfile getCurrentJwtProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // 在 JwtAuthenticationFilter 是直接塞 JwtProfile
        // 所以直接轉型拿出來就好！
        if (principal instanceof JwtProfile profile) {
            return profile;
        }

        return null;
    }

    /**
     * 取得當前登入使用者的 User ID
     */
    public static Integer getCurrentUserId() {
        JwtProfile profile = getCurrentJwtProfile();
        return profile != null ? profile.getUserId() : null;
    }

    /**
     * 取得當前登入使用者的 Email
     */
    public static String getCurrentUserEmail() {
        JwtProfile profile = getCurrentJwtProfile();
        return profile != null ? profile.getEmail() : null;
    }
    
    /**
     * 取得當前登入使用者的 Role
     */
    public static Integer getCurrentUserRole() {
        JwtProfile profile = getCurrentJwtProfile();
        return profile != null ? profile.getRole() : null;
    }

}
