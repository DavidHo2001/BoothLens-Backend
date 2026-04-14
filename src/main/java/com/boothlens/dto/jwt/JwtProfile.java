package com.boothlens.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtProfile {
    private Integer userId;
    private String email;
    private int role; //1: client, 2: contractor, 3: admin, 4: superadmin //TODO 改做role id
}
