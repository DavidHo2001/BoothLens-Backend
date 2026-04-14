package com.boothlens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boothlens.entity.EmailVerification;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {

    Optional<EmailVerification> findFirstByEmailAndVerificationCodeOrderByCreatedAtDesc(String email, String verificationCode);

    void deleteByEmail(String email);

    boolean existsByEmail(String email);

    /** True if a row exists for this email with created_at strictly after {@code since} (UTC instant). */
    boolean existsByEmailAndCreatedAtAfter(String email, Instant since);
} 