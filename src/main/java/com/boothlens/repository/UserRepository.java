package com.boothlens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.boothlens.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    User findFirstById(Integer id);
    
    boolean existsByEmail(String email);

    List<User> findByRoleIdAndIsBlockedFalseOrderByIdAsc(Integer roleId);
}
