package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.Permission;
import java.util.List;

public interface PermissionService {

    List<Permission> getAllPermissions();
    Permission getPermissionById(Long id);
    Permission addPermission(Permission permission);
    Permission updatePermission(Long id, Permission permission);
    void deletePermission(Long id);
}
