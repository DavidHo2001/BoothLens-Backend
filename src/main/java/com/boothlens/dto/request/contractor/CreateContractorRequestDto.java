package com.boothlens.dto.request.contractor;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateContractorRequestDto {
    /**
     * Optional source reference (for example BA-123) when user creates follow-up request after scanning.
     */
    private String sourceReferenceCode;

    @NotBlank(message = "Please provide request details for contractor")
    private String requestInfo;
}
