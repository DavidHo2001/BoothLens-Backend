package com.boothlens.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
@Data
@AllArgsConstructor
public class UserRoleResponse {
    private Integer id;
    private String username;
    private String role;
    private Integer roleId;
    private Boolean isBlocked;
}
