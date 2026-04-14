package com.boothlens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boothlens.entity.UserCredits;

import java.util.Optional;

@Repository
public interface UserCreditsRepository extends JpaRepository<UserCredits, Integer> {
    Optional<UserCredits> findByUserId(Integer userId);
}
