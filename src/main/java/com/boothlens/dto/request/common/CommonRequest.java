package com.boothlens.dto.request.common;

import com.boothlens.constant.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommonRequest {
    @NotNull(message = "requestType is required")
    private RequestType requestType;

    private String sourceReferenceCode;

    @NotBlank(message = "Please provide request details")
    private String requestInfo;
}
