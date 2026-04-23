package com.boothlens.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.boothlens.constant.SystemConstants;
import com.boothlens.dto.request.booth.BoothAnalysisRequest;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.service.analysis.BoothAnalysisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/v1/booth")
public class BoothController {
    
    @Autowired
    private BoothAnalysisService boothAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<String>> analyzeBooth(
            @RequestParam("images") MultipartFile[] images,
            @ModelAttribute("requestData") BoothAnalysisRequest requestData) {
        String referenceCode = boothAnalysisService.analyzeBooth(images, requestData);
        return ResponseEntity.ok(ApiResponse.success(SystemConstants.MSG_ESTIMATION_SUCCESS, referenceCode));
    }
   
}

