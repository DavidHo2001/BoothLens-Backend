package com.boothlens.service;

import com.boothlens.constant.SystemConstants;
import com.boothlens.entity.EmailVerification;
import com.boothlens.repository.EmailVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailVerificationService {
    
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Generates a 4-digit verification code
     */
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }
    
    /**
     * Sends verification code to the specified email
     */
    @Transactional
    public Boolean sendVerificationCode(String email) {
        if (!isSendAvailable(email)) {
            return false;
        }

        // Delete any existing verification codes for this email
        emailVerificationRepository.deleteByEmail(email);
        
        // Generate new verification code
        String verificationCode = generateVerificationCode();
        
        // Save verification code to database
        EmailVerification emailVerification = new EmailVerification(email, verificationCode);
        emailVerificationRepository.save(emailVerification);
        try {
            sendEmail(email, verificationCode);
        } catch (Exception e) {
            throw new IllegalArgumentException(SystemConstants.ERR_EMAIL_VERIFICATION_FAILED);
        }
        return true;
    }
    
    /**
     * Verifies the email and code combination
     */
    @Transactional
    public boolean verifyCode(String email, String code) {
        Optional<EmailVerification> verification = emailVerificationRepository.findFirstByEmailAndVerificationCodeOrderByCreatedAtDesc(email, code);
        
        if (verification.isPresent()) {
            EmailVerification emailVerification = verification.get();
            
            if (emailVerification.isExpired()) {
                emailVerificationRepository.deleteByEmail(email);
                return false;
            }            
            emailVerificationRepository.deleteByEmail(email);
            return true;
        }
        
        return false;
    }
    
    /**
     * Sends the verification email
     */
    private void sendEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Email Verification Code - BoothLens");
        message.setText("Your verification code is: " + verificationCode + "\n\n" +
                       "This code will expire in 10 minutes.\n" +
                       "If you didn't request this code, please ignore this email.\n\n" +
                       "Best regards,\n" +
                       "BoothLens");
        
        mailSender.send(message);
    }

    /**
     * @return true if no verification row exists for this email with {@code created_at} within the last minute.
     */
    private boolean isSendAvailable(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        Instant oneMinuteAgo = Instant.now().minus(1, ChronoUnit.MINUTES);
        boolean recentSendExists = emailVerificationRepository.existsByEmailAndCreatedAtAfter(email.trim(), oneMinuteAgo);
        return !recentSendExists;
    }
} 