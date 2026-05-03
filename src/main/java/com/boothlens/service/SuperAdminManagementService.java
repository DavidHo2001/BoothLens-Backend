package com.boothlens.service;

import com.boothlens.constant.UserRole;
import com.boothlens.dto.request.superadmin.UserInfoRequest;
import com.boothlens.dto.request.superadmin.UserSearchRequestDto;
import com.boothlens.dto.response.UserRoleResponse;
import com.boothlens.dto.response.pagination.PaginatedResponseDto;
import com.boothlens.entity.User;
import com.boothlens.mapper.UserMapper;
import com.boothlens.repository.UserRepository;
import com.boothlens.specification.UserSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuperAdminManagementService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public SuperAdminManagementService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public PaginatedResponseDto<UserRoleResponse> searchUsers(UserSearchRequestDto request) {
        Page<User> page = userRepository.findAll(
                UserSpecifications.withFilters(request.getKeyword(), request.getRoleId(), request.getIsBlocked()),
                request.getPageable()
        );

        return new PaginatedResponseDto<>(page.map(userMapper::toUserRoleResponse));
    }

    @Transactional
    public boolean adjustUserRole(Integer userId, UserInfoRequest request) {
        if (request.getUserId() != null && !request.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Payload userId does not match path userId");
        }
        if (request.getRoleId() == null && request.getIsBlocked() == null) {
            throw new IllegalArgumentException("No changes detected");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean hasChanges = false;
        if (request.getRoleId() != null) {
            UserRole.fromValue(request.getRoleId()); // Validate roleId before updating.
            if (!request.getRoleId().equals(user.getRoleId())) {
                user.setRoleId(request.getRoleId());
                hasChanges = true;
            }
        }

        if (request.getIsBlocked() != null && !request.getIsBlocked().equals(user.getIsBlocked())) {
            user.setIsBlocked(request.getIsBlocked());
            hasChanges = true;
        }

        if (!hasChanges) {
            throw new IllegalArgumentException("No changes detected");
        }

        User saved = userRepository.saveAndFlush(user);
        boolean roleUpdated = request.getRoleId() == null || request.getRoleId().equals(saved.getRoleId());
        boolean blockStatusUpdated = request.getIsBlocked() == null || request.getIsBlocked().equals(saved.getIsBlocked());
        return roleUpdated && blockStatusUpdated;
    }
}
