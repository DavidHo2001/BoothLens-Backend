package com.boothlens.service;

import com.boothlens.builder.UserBuilder;
import com.boothlens.constant.SystemConstants;
import com.boothlens.dto.request.RegistrationRequest;
import com.boothlens.entity.User;
import com.boothlens.entity.UserCredits;
import com.boothlens.entity.UserProfile;
import com.boothlens.repository.UserCreditsRepository;
import com.boothlens.repository.UserProfileRepository;
import com.boothlens.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserCreditsRepository userCreditsRepository;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void sendVerificationCode(String email) {


        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(SystemConstants.ERR_EMAIL_EXISTS);
        }

        Boolean isSent = emailVerificationService.sendVerificationCode(email);
        if (!isSent) {
            throw new IllegalArgumentException(SystemConstants.ERR_VERIFICATION_CODE_TOO_SOON);
        }
    }

    @Transactional
    public Boolean registerUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(SystemConstants.ERR_EMAIL_EXISTS);
        }

        boolean isCodeValid = emailVerificationService.verifyCode(
                request.getEmail(),
                request.getVerificationCode());

        if (!isCodeValid) {
            throw new IllegalArgumentException(SystemConstants.ERR_INVALID_VERIFICATION_CODE);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = UserBuilder.buildBasicUser(request, encodedPassword, 1);

        User savedUser = userRepository.saveAndFlush(user);

        UserProfile profile = UserBuilder.buildBasicUserProfile(request, savedUser);
        userProfileRepository.save(profile);

        UserCredits userCredits = UserBuilder.buildBasicUserCredits(savedUser);
        userCreditsRepository.save(userCredits);

        log.info("User registered successfully: {} - {}", savedUser.getUsername(), savedUser.getEmail());

        return true;
    }
}
