package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RolePermissionsResponse {

    private Long roleId;
    private String roleName;
    private String subTitle;
    private List<ModuleDTO> modules;

    @Getter
    @Setter
    public static class ModuleDTO {
        private Long moduleId;
        private String moduleName;
        private List<PermissionDTO> permissions;
    }

    @Getter
    @Setter
    public static class PermissionDTO {
        private Long permissionId;
        private String label;
        private boolean allowed;
        private String url;
    }
}
