package com.boothlens.dto.request.support;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSupportRequest {
    @NotBlank(message = "Please provide request details for support")
    private String requestInfo;
    
}
