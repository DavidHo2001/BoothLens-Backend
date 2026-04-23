package com.boothlens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.boothlens.entity.UserRequest;

import java.util.List;

@Repository
public interface UserRequestRepository extends JpaRepository<UserRequest, Integer>, JpaSpecificationExecutor<UserRequest> {
    UserRequest findByReferenceCode(String referenceCode);
    List<UserRequest> findByUserId(Integer userId);
    List<UserRequest> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
