package com.boothlens.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate birth_date;
    private String sex;
    private String address;
    private String city;
    private String zip;
    private String country;
}
