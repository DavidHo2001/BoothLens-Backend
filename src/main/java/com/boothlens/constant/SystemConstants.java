package com.boothlens.constant;

public class SystemConstants {
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MIN_LENGTH_PROFILE = 8;
    public static final int VERIFICATION_CODE_LENGTH = 4;

    public static final String GENERIC_INPUT_ERROR = "Invalid input";

    public static final String MSG_LOGIN_SUCCESS = "Login successful";
    public static final String MSG_LOGOUT_SUCCESS = "Logged out";
    public static final String MSG_REGISTRATION_SUCCESS = "User registered successfully";
    public static final String MSG_PROFILE_UPDATE_SUCCESS = "Profile updated successfully. Please login again";
    public static final String MSG_CODE_SENT_SUCCESS = "Verification code sent successfully";
    public static final String MSG_ESTIMATION_SUCCESS = "Booth analysis completed successfully";

    public static final String ERR_INVALID_EMAIL_PASSWORD = "Invalid email or password";
    public static final String ERR_EMAIL_NOT_VERIFIED = "Please verify your email address before logging in";
    public static final String ERR_EMAIL_EXISTS = "Email already exists";
    public static final String ERR_INVALID_VERIFICATION_CODE = "Invalid or expired verification code";
    public static final String ERR_VERIFICATION_CODE_TOO_SOON = "Please wait one minute before requesting another verification code";
    public static final String ERR_EMAIL_VERIFICATION_FAILED = "Failed to send verification code";

    public static final String ERR_PASSWORD_MISMATCH = "Password and confirm password do not match";
    public static final String ERR_INCORRECT_OLD_PASSWORD = "Old password is incorrect";
    public static final String ERR_UNAUTHORIZED_PROFILE_UPDATE = "Cannot update profile for different user";
    public static final String ERR_USER_NOT_FOUND = "User not found";
    public static final String ERR_PROFILE_NOT_FOUND = "Profile not found";
    public static final String ERR_USER_BLOCKED = "User is blocked, please contact support";

    public static final String ERR_USER_NOT_AUTHENTICATED = "User not authenticated";
    public static final String ERR_INSUFFICIENT_CREDITS = "Insufficient credits";
    public static final String ERR_USER_CREDITS_NOT_FOUND = "User credits not found";
    public static final String ERR_USER_REQUEST_NOT_FOUND = "User request not found";

    private SystemConstants() {
    }
}
