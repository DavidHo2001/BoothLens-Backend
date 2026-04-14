package com.boothlens.builder;

import com.boothlens.dto.request.RegistrationRequest;
import com.boothlens.entity.User;
import com.boothlens.entity.UserCredits;
import com.boothlens.entity.UserProfile;

public final class UserBuilder {

    private UserBuilder() {
    }


    public static User buildBasicUser(RegistrationRequest request, String encodedPassword, int roleId) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setVerificationStatus(true);
        user.setRoleId(roleId);
        return user;
    }
    public static UserProfile buildBasicUserProfile(RegistrationRequest request, User user) {
        UserProfile profile = new UserProfile();
        profile.setPhone(blankToNull(request.getPhone()));
        profile.setUserId(user.getId());

        return profile;
    }

    // Default values are 0 for both balance and token
    public static UserCredits buildBasicUserCredits(User user) {
        UserCredits credits = new UserCredits();
        credits.setUserId(user.getId());
        return credits;
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
