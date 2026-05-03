package com.boothlens.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boothlens.dto.request.UpdateProfileRequest;
import com.boothlens.dto.request.ResetPasswordRequest;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.dto.response.ProfileResponse;
import com.boothlens.service.ProfileService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;

    @GetMapping("/fetchProfile")
    public ResponseEntity<ApiResponse<ProfileResponse>> fetchProfile() {
        ProfileResponse profile = profileService.fetchProfile();
        return ResponseEntity.ok(ApiResponse.success(null,profile));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            HttpServletResponse response) {
        String message = profileService.updateProfile(request, response);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PatchMapping("/reset-password/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> resetUserPassword(@PathVariable Integer userId, @RequestBody ResetPasswordRequest request) {
        boolean isPasswordReset = profileService.resetUserPassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success(
                isPasswordReset ? "User password updated successfully" : "User password reset failed",
                isPasswordReset
        ));
    }
}
