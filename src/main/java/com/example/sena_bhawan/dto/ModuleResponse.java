package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ModuleResponse {
    private Long moduleId;
    private String moduleName;
    private List<PermissionResponse> permissions;

    public ModuleResponse(Long moduleId, String moduleName, List<PermissionResponse> permissions) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.permissions = permissions;
    }

    // getters & setters
}

