package com.boothlens.dto.response.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private Integer id;
    private Integer userId;
    private String referenceCode;
    private String requestInfo;
    private String status;
    private String resultInfo;
    private Instant createdAt;
    private Instant updatedAt;
}
