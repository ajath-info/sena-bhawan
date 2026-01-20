package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserPermissionsResponse {

    private Long userId;
    private String username;
    private List<ModuleResponse> modules;

    public UserPermissionsResponse(Long userId, String username, List<ModuleResponse> modules) {
        this.userId = userId;
        this.username = username;
        this.modules = modules;
    }

    // getters & setters
}
