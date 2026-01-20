package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.UserPermissionsResponse;
import com.example.sena_bhawan.service.UserPermissionService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserPermissionController {

    private final UserPermissionService service;

    public UserPermissionController(UserPermissionService service) {
        this.service = service;
    }

    @GetMapping("/{userId}/permissions")
    public UserPermissionsResponse getPermissions(@PathVariable Long userId) {
        return service.getUserPermissions(userId);
    }
}

