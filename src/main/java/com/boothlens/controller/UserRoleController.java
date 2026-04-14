package com.boothlens.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.boothlens.service.UserRoleService;
import com.boothlens.dto.response.UserRoleResponse;
import com.boothlens.dto.request.UserRoleRequest;
import com.boothlens.dto.response.ApiResponse;


import java.util.List;
@RestController
@RequestMapping("/api/superadmin/user-role") //Only superadmin can access this endpoint
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<UserRoleResponse>>> getUserRoleList() {
        return ResponseEntity.ok(ApiResponse.success("User role list fetched successfully", userRoleService.getUserRoleList()));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Boolean>> updateUserRole(@RequestBody UserRoleRequest request) {
        userRoleService.updateUserRole(request.getUserId(), request.getRoleId(), request.getIsBlocked());
        return ResponseEntity.ok(ApiResponse.success("User role and blocked status updated successfully"));
    }
}
