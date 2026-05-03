package com.boothlens.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boothlens.constant.SystemConstants;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.logging.LogEndpointRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.boothlens.service.EmailVerificationService;

@RestController
@RequestMapping("/api/email")
public class EmailControlller { 

    @Autowired
    private EmailVerificationService emailVerificationService;

    //Generic for different purposes
    @LogEndpointRequest
    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestBody String email) {
        if (!emailVerificationService.isEmailExistsInOurSystem(email)) {
            return ResponseEntity.ok(ApiResponse.success(SystemConstants.MSG_CODE_SENT_SUCCESS_IF_EMAIL_EXISTS));
        }
        emailVerificationService.sendVerificationCode(email);
        return ResponseEntity.ok(ApiResponse.success(SystemConstants.MSG_CODE_SENT_SUCCESS_IF_EMAIL_EXISTS));
    }
}
