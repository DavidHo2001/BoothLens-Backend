package com.boothlens.dto.response;

import com.boothlens.dto.response.request.UserRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse {
    private List<UserRequestDto> requestDto;
    private UserCreditsResponse creditsResponse;
    private UserRoleResponse roleResponse;
    private ProfileResponse profileResponse;
}
