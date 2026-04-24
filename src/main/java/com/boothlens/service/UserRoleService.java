package com.boothlens.service;

import com.boothlens.constant.UserRole;
import com.boothlens.dto.response.UserRoleResponse;
import com.boothlens.entity.User;
import com.boothlens.mapper.UserMapper;
import com.boothlens.repository.UserRepository;
import com.boothlens.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRoleService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;


    public Boolean updateUserRole(Integer userId, Integer roleId, Boolean isBlocked) {
        UserRole.fromValue(roleId); //Validation of roleId

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRoleId().equals(roleId) && user.getIsBlocked().equals(isBlocked)) {
            throw new IllegalArgumentException("No changes detected");
        }

        user.setRoleId(roleId);
        user.setIsBlocked(isBlocked);
        User saved = userRepository.saveAndFlush(user);

        if (saved.getRoleId().equals(roleId) && saved.getIsBlocked().equals(isBlocked)) {
            return true;
        }
        throw new IllegalArgumentException("User role/status update failed");
    }
    
    public List<UserRoleResponse> getUserRoleList() {
        List<User> users = userRepository.findAll();
    
        return users.stream()
                .map(userMapper::toUserRoleResponse)
                .collect(Collectors.toList());
    }

    public UserRoleResponse getCurrentUserRole() {
        Integer userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.toUserRoleResponse(user);
    }
}
