package com.boothlens.service;

import com.boothlens.constant.SystemConstants;
import com.boothlens.dto.request.UpdateProfileRequest;
import com.boothlens.dto.response.ProfileResponse;
import com.boothlens.entity.User;
import com.boothlens.entity.UserProfile;
import com.boothlens.repository.UserProfileRepository;
import com.boothlens.repository.UserRepository;
import com.boothlens.util.SecurityUtils;
import com.boothlens.util.ValidationUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProfileService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public ProfileResponse fetchProfile() {
        Integer userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        User user = userRepository.findFirstById(userId);
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
            .orElse(null);

        return new ProfileResponse(
            user.getEmail(),
            user.getUsername(),
            profile.getFirstName(),
            profile.getLastName(),
            profile.getPhone(),
            profile.getBirthDate(),
            profile.getSex(),
            profile.getAddress(),
            profile.getCity(),
            profile.getZip(),
            profile.getCountry()
        );
    }

    @Transactional
    public String updateProfile(UpdateProfileRequest request, HttpServletResponse response) {
        String currentEmail = SecurityUtils.getCurrentUserEmail();
        if (currentEmail == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        
        if (!currentEmail.equals(request.getEmail())) {
            throw new IllegalArgumentException(SystemConstants.ERR_UNAUTHORIZED_PROFILE_UPDATE);
        }
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException(SystemConstants.ERR_USER_NOT_FOUND));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(SystemConstants.ERR_PROFILE_NOT_FOUND));

        if (isPasswordChangeRequested(request)) {
            validatePasswordChange(request, user);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getZip() != null) {
            profile.setZip(request.getZip());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }
        if (request.getBirth_date() != null) {
            profile.setBirthDate(request.getBirth_date());
        }
        if (request.getSex() != null) {
            profile.setSex(request.getSex());
        }

        userProfileRepository.save(profile);
        userRepository.save(user);
        
        log.info("Profile updated successfully for user: {}", user.getEmail());
        
        return SystemConstants.MSG_PROFILE_UPDATE_SUCCESS;
    }

    private boolean isPasswordChangeRequested(UpdateProfileRequest request) {
        return request.getOldPassword() != null && !request.getOldPassword().isEmpty() &&
               request.getPassword() != null && !request.getPassword().isEmpty() &&
               request.getConfirmPassword() != null && !request.getConfirmPassword().isEmpty();
    }

    private void validatePasswordChange(UpdateProfileRequest request, User user) {
        ValidationUtils.validatePasswordMatch(request.getPassword(), request.getConfirmPassword());
        
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException(SystemConstants.ERR_INCORRECT_OLD_PASSWORD);
        }
        
        ValidationUtils.validatePasswordStrength(request.getPassword(), SystemConstants.PASSWORD_MIN_LENGTH_PROFILE);
    }
}
