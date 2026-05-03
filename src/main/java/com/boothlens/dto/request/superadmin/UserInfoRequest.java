package com.boothlens.dto.request.superadmin;

import lombok.Data;

@Data
public class UserInfoRequest {
    private Integer userId; //map to id in user_tb
    private Integer roleId;
    private Boolean isBlocked;
    
}
