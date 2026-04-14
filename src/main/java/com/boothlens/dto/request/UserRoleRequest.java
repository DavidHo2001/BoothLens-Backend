package com.boothlens.dto.request;
import lombok.Data;

@Data
public class UserRoleRequest {
    private Integer userId;
    private Integer roleId;
    private Boolean isBlocked;
}