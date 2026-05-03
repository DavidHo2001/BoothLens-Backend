package com.boothlens.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String verificationCode; //Need to send verification code to email and enter it here
    private String newPassword;
    private String confirmNewPassword;
}
