package com.boothlens.dto.response;

import com.boothlens.constant.RequestType;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CommonRequestResponse {
    private String requestReferenceCode;
    private RequestType requestType;
}
