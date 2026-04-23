package com.boothlens.dto.response.contractor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorMatchResponseDto {
    private String contractorRequestReferenceCode;
    private String sourceReferenceCode;
    private Integer contractorUserId;
    private String contractorName;
    private String contractorEmail;
    private String message;
}
