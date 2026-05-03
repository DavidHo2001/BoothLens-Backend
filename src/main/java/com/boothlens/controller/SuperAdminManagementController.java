package com.boothlens.controller;

import com.boothlens.dto.request.superadmin.UserInfoRequest;
import com.boothlens.dto.request.superadmin.UserSearchRequestDto;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.dto.response.UserRoleResponse;
import com.boothlens.dto.response.pagination.PaginatedResponseDto;
import com.boothlens.service.SuperAdminManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminManagementController {

    private final SuperAdminManagementService superAdminManagementService;

    public SuperAdminManagementController(SuperAdminManagementService superAdminManagementService) {
        this.superAdminManagementService = superAdminManagementService;
    }

    //User search
    @PostMapping("/users/search")
    public ResponseEntity<ApiResponse<PaginatedResponseDto<UserRoleResponse>>> searchUsers(
            @RequestBody UserSearchRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched users",
                superAdminManagementService.searchUsers(request)
        ));
    }

    //Adjust user role and blocked status
    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> adjustUserRole(@PathVariable Integer userId, @RequestBody UserInfoRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "User role and blocked status adjusted successfully",
                superAdminManagementService.adjustUserRole(userId, request)
        ));
    }
}
