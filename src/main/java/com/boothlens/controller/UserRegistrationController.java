package com.boothlens.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boothlens.constant.SystemConstants;
import com.boothlens.dto.request.RegistrationRequest;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.service.UserRegistrationService;
import com.boothlens.logging.LogEndpointRequest;

@RestController
@RequestMapping("/api/userRegistration")
public class UserRegistrationController {
    
    @Autowired
    private UserRegistrationService userRegistrationService;

    @LogEndpointRequest
    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestBody String email) {
        userRegistrationService.sendVerificationCode(email);
        return ResponseEntity.ok(ApiResponse.success(SystemConstants.MSG_CODE_SENT_SUCCESS));
    }

    @LogEndpointRequest
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> registerUser(@RequestBody RegistrationRequest request) {
        Boolean isRegistered = userRegistrationService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(SystemConstants.MSG_REGISTRATION_SUCCESS, isRegistered));
    }
}
