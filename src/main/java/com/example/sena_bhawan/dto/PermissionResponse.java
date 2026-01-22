package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionResponse {
    private Long permissionId;
    private String label;
    private Boolean allowed;
    private String url;

    public PermissionResponse(Long permissionId, String label, Boolean allowed, String url) {
        this.permissionId = permissionId;
        this.label = label;
        this.allowed = allowed;
        this.url = url;
    }


}

