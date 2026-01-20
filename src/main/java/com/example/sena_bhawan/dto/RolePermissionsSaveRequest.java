package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RolePermissionsSaveRequest {

    private List<PermissionEntry> permissions;

    @Getter
    @Setter
    public static class PermissionEntry {
        private Long moduleId;
        private Long permissionId;
        private boolean allowed;
    }
}
