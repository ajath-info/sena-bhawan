package com.example.sena_bhawan.dto;

public interface UserPermissionProjection {
    Long getModuleId();
    String getModuleName();
    Long getPermissionId();
    String getLabel();
    Boolean getAllowed();
    String getUrl();
}
