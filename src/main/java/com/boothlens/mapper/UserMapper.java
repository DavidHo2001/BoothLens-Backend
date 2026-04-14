package com.boothlens.mapper;

import com.boothlens.entity.User;
import com.boothlens.dto.response.UserRoleResponse;
import com.boothlens.dto.response.UserCreditsResponse;
import com.boothlens.entity.UserCredits;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // "spring" allows you to @Autowired this mapper
public interface UserMapper {

    // MapStruct automatically maps fields with the exact same name (like id, username, isBlocked).
    // You only need @Mapping when the source and target names/types are different.
    
    @Mapping(target = "role", expression = "java(com.boothlens.constant.UserRole.fromValue(user.getRoleId()).getName())") // Custom logic for the String role
    UserRoleResponse toUserRoleResponse(User user);

    UserCreditsResponse toUserCreditsResponse(UserCredits userCredits);
}
