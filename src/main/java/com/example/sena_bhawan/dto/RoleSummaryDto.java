package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class RoleSummaryDto {
    private Long roleId;
    private String roleName;
    private String subTitle;
    private int userCount;                // Random 10–100
    private int allowedPermissionCount;   // Count from role_permissions table
}
