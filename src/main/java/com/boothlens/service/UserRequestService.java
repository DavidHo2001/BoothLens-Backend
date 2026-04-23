package com.boothlens.service;

import com.boothlens.constant.ReferenceCode;
import com.boothlens.dto.request.admin.UpdateUserRequestAdminDto;
import com.boothlens.dto.request.admin.UserRequestSearchRequestDto;
import com.boothlens.dto.request.contractor.CreateContractorRequestDto;
import com.boothlens.dto.response.contractor.ContractorMatchResponseDto;
import com.boothlens.dto.response.pagination.PaginatedResponseDto;
import com.boothlens.dto.response.request.UserRequestDto;
import com.boothlens.entity.User;
import com.boothlens.entity.UserRequest;
import com.boothlens.repository.UserRepository;
import com.boothlens.repository.UserRequestRepository;
import com.boothlens.specification.UserRequestSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import com.boothlens.constant.SystemConstants;

@Service
@Slf4j
public class UserRequestService {
    private static final Set<String> ALLOWED_STATUS = Set.of(
            "PENDING", "PROCESSING", "COMPLETED", "FAILED", "REJECTED"
    );

    @Autowired
    private UserRequestRepository userRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRecordService fileRecordService;

    @Transactional
    public UserRequest createUserBoothAnalysisRequest(Integer userId, String requestInfo) {
        UserRequest userRequest = new UserRequest();
        userRequest.setUserId(userId);
        userRequest.setRequestInfo(requestInfo);
        userRequest.setStatus("PENDING");
        userRequest.setCreatedAt(Instant.now());
        userRequest.setUpdatedAt(Instant.now());

        UserRequest savedUserRequest = userRequestRepository.saveAndFlush(userRequest);
        String referenceCode = ReferenceCode.BOOTH_ANALYSIS.getReferenceCode(savedUserRequest.getId());
        savedUserRequest.setReferenceCode(referenceCode);
        savedUserRequest = userRequestRepository.saveAndFlush(savedUserRequest);
        log.info("Created UserRequest with id={} and referenceCode={}", savedUserRequest.getId(), referenceCode);
        return savedUserRequest;
    }

    public void updateUserRequestStatus(Integer userId, Integer requestId, String status) {
        validateStatus(status);
        String normalizedStatus = status.toUpperCase();
        UserRequest userRequest = userRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(SystemConstants.ERR_USER_REQUEST_NOT_FOUND));
        userRequest.setStatus(normalizedStatus);
        userRequest.setUpdatedAt(Instant.now());
        userRequestRepository.save(userRequest);
    }

    public List<UserRequestDto> getCurrentUserRequests(Integer userId) {
        return userRequestRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserRequest getRequestByReferenceCode(String referenceCode) {
        UserRequest userRequest = userRequestRepository.findByReferenceCode(referenceCode);
        if (userRequest == null) {
            throw new EntityNotFoundException(SystemConstants.ERR_USER_REQUEST_NOT_FOUND);
        }
        return userRequest;
    }

    @Transactional
    public ContractorMatchResponseDto createContractorRequest(Integer userId,
                                                              CreateContractorRequestDto request,
                                                              MultipartFile[] files) {
        // Optional linkage to previous scan request.
        if (StringUtils.hasText(request.getSourceReferenceCode())) {
            UserRequest sourceRequest = getRequestByReferenceCode(request.getSourceReferenceCode());
            if (!sourceRequest.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Source reference does not belong to current user");
            }
        }

        UserRequest contractorRequest = new UserRequest();
        contractorRequest.setUserId(userId);
        contractorRequest.setStatus("PENDING");
        contractorRequest.setRequestInfo(buildContractorRequestInfo(request));
        contractorRequest.setCreatedAt(Instant.now());
        contractorRequest.setUpdatedAt(Instant.now());
        contractorRequest = userRequestRepository.saveAndFlush(contractorRequest);

        String contractorRefCode = ReferenceCode.CONTRACTOR_REQUEST.getReferenceCode(contractorRequest.getId());
        contractorRequest.setReferenceCode(contractorRefCode);
        contractorRequest = userRequestRepository.saveAndFlush(contractorRequest);

        // Attach files to contractor request when provided.
        fileRecordService.uploadImagesAndSaveRecords(files, userId, contractorRefCode);

        User matchedContractor = pickContractor(contractorRequest.getId());
        return ContractorMatchResponseDto.builder()
                .contractorRequestReferenceCode(contractorRefCode)
                .sourceReferenceCode(request.getSourceReferenceCode())
                .contractorUserId(matchedContractor != null ? matchedContractor.getId() : null)
                .contractorName(matchedContractor != null ? matchedContractor.getUsername() : null)
                .contractorEmail(matchedContractor != null ? matchedContractor.getEmail() : null)
                .message(matchedContractor != null
                        ? "Contractor matched successfully"
                        : "Request created. No contractor available right now")
                .build();
    }

    public PaginatedResponseDto<UserRequestDto> adminSearch(UserRequestSearchRequestDto request) {
        Page<UserRequest> page = userRequestRepository.findAll(
                UserRequestSpecifications.withFilters(request.getKeyword(), request.getStatus()),
                request.getPageable()
        );
        return new PaginatedResponseDto<>(page.map(this::toDto));
    }

    @Transactional
    public UserRequestDto adminUpdateRequest(Integer requestId, UpdateUserRequestAdminDto request) {
        UserRequest userRequest = userRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(SystemConstants.ERR_USER_REQUEST_NOT_FOUND));

        if (StringUtils.hasText(request.getStatus())) {
            validateStatus(request.getStatus());
            userRequest.setStatus(request.getStatus().toUpperCase());
        }
        if (request.getResultInfo() != null) {
            userRequest.setResultInfo(request.getResultInfo());
        }
        userRequest.setUpdatedAt(Instant.now());
        return toDto(userRequestRepository.saveAndFlush(userRequest));
    }

    private UserRequestDto toDto(UserRequest request) {
        return UserRequestDto.builder()
                .id(request.getId())
                .userId(request.getUserId())
                .referenceCode(request.getReferenceCode())
                .requestInfo(request.getRequestInfo())
                .status(request.getStatus())
                .resultInfo(request.getResultInfo())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    private void validateStatus(String status) {
        if (!StringUtils.hasText(status) || !ALLOWED_STATUS.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Invalid request status");
        }
    }

    private String buildContractorRequestInfo(CreateContractorRequestDto request) {
        StringBuilder builder = new StringBuilder("Contractor request");
        if (StringUtils.hasText(request.getSourceReferenceCode())) {
            builder.append(" | SourceRef: ").append(request.getSourceReferenceCode().trim());
        }
        builder.append(" | Message: ").append(request.getRequestInfo().trim());
        return builder.toString();
    }

    private User pickContractor(Integer requestId) {
        List<User> contractors = userRepository.findByRoleIdAndIsBlockedFalseOrderByIdAsc(2);
        if (contractors.isEmpty()) {
            return null;
        }
        int index = requestId % contractors.size();
        return contractors.get(index);
    }
}
