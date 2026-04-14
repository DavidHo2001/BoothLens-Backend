package com.boothlens.service;

import com.boothlens.constant.ReferenceCode;
import com.boothlens.entity.UserRequest;
import com.boothlens.repository.UserRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import jakarta.persistence.EntityNotFoundException;
import com.boothlens.constant.SystemConstants;
@Service
@Slf4j
public class UserRequestService {

    @Autowired
    private UserRequestRepository userRequestRepository;

    @Transactional
    public UserRequest createUserBoothAnalysisRequest(Integer userId, String requestInfo) {
        // 1. Create new UserRequest instance
        UserRequest userRequest = new UserRequest();
        userRequest.setUserId(userId);
        userRequest.setRequestInfo(requestInfo);
        userRequest.setStatus("PENDING"); // default status
        userRequest.setCreatedAt(Instant.now());
        userRequest.setUpdatedAt(Instant.now());

        // 2. Save to generate ID
        UserRequest savedUserRequest = userRequestRepository.saveAndFlush(userRequest);

        // 3. Set referenceCode using ReferenceCode enum (assuming BOOTH_ANALYSIS here)
        String referenceCode = ReferenceCode.BOOTH_ANALYSIS.getReferenceCode(savedUserRequest.getId());
        savedUserRequest.setReferenceCode(referenceCode);

        // 4. Save again to update referenceCode
        savedUserRequest = userRequestRepository.saveAndFlush(savedUserRequest);

        log.info("Created UserRequest with id={} and referenceCode={}", savedUserRequest.getId(), referenceCode);

        return savedUserRequest;
    }

    public void updateUserRequestStatus(Integer userId, Integer requestId, String status) {
        UserRequest userRequest = userRequestRepository.findById(requestId).orElseThrow(() -> 
        new EntityNotFoundException(SystemConstants.ERR_USER_REQUEST_NOT_FOUND));
        userRequest.setStatus(status);
        userRequestRepository.save(userRequest);
    }
}
