package com.boothlens.controller;

import com.boothlens.constant.SystemConstants;
import com.boothlens.dto.request.LoginRequest;
import com.boothlens.dto.response.LoginResponse;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(SystemConstants.MSG_LOGIN_SUCCESS, loginResponse));
    }

    //TODO
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(SystemConstants.MSG_LOGOUT_SUCCESS));
    }
}
