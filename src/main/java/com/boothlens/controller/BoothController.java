package com.boothlens.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.boothlens.constant.SystemConstants;
import com.boothlens.dto.request.booth.BoothAnalysisRequest;
import com.boothlens.dto.request.openrouter.OpenRouterRequest;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.dto.response.booth.BoothAnalysisResponse;
import com.boothlens.service.analysis.BoothAnalysisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/booth")
public class BoothController {
    
    @Autowired
    private BoothAnalysisService boothAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<BoothAnalysisResponse> analyzeBooth(
            @RequestPart("images") MultipartFile[] images,
            @RequestPart("requestData") BoothAnalysisRequest requestData,
            @RequestHeader("Authorization") String authorization) {
        String jwt = authorization.substring(7);
        BoothAnalysisResponse boothAnalysisResponse = boothAnalysisService.analyzeBooth(images, requestData);
        return ResponseEntity.ok(boothAnalysisResponse);
    }
   
}

