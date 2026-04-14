package com.boothlens.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer userId;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private int role;
}
