package com.boothlens.service;

import com.boothlens.constant.SystemConstants;
import com.boothlens.dto.request.LoginRequest;
import com.boothlens.dto.response.LoginResponse;
import com.boothlens.dto.response.UserResponse;
import com.boothlens.entity.User;
import com.boothlens.entity.UserProfile;
import com.boothlens.repository.UserProfileRepository;
import com.boothlens.repository.UserRepository;
import com.boothlens.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    
    @Value("${jwt.expiration}")
    private int jwtExpiration;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new IllegalStateException(SystemConstants.ERR_INVALID_EMAIL_PASSWORD));
        
        // 驗證使用者狀態 (封鎖/信箱驗證)
        validateUserStatus(user);
        
        // 驗證密碼
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalStateException(SystemConstants.ERR_INVALID_EMAIL_PASSWORD);
        }
        
        // 產生 JWT
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRoleId());
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        return new LoginResponse(
            token,
            user.getEmail(),
            user.getId(),
            user.getUsername(),
            user.getRoleId()
        );
    }

    public void logout(HttpServletRequest request) {

    }


    public UserResponse getCurrentUser() {
        if (!SecurityUtils.isAuthenticated()) {
            return null;
        }
        
        Integer userId = SecurityUtils.getCurrentUserId();
        if (userId == null) return null;
        
        User user = userRepository.findFirstById(userId);

        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            profile.getPhone(),
            profile.getFirstName(),
            profile.getLastName(),
            user.getRoleId()
        );
    }

    // --- Private Helper Methods ---

    private void validateUserStatus(User user) {
        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new IllegalStateException(SystemConstants.ERR_USER_BLOCKED);
        }
        if (!user.isVerificationStatus()) {
            throw new IllegalStateException(SystemConstants.ERR_EMAIL_NOT_VERIFIED);
        }
    }

}
