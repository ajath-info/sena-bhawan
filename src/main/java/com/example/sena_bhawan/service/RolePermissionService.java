package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.RolePermissionsResponse;
import com.example.sena_bhawan.dto.RolePermissionsSaveRequest;

public interface RolePermissionService {

    RolePermissionsResponse getPermissionsForRole(Long roleId);

    void saveRolePermissions(Long roleId, RolePermissionsSaveRequest request);

    void resetPermissions(Long roleId);
}
