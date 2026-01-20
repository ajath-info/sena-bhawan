package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.RolePermissionsResponse;
import com.example.sena_bhawan.dto.RolePermissionsSaveRequest;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepo;
    private final ModuleRepository moduleRepo;
    private final PermissionRepository permissionRepo;
    private final RolePermissionRepository rolePermissionRepo;

    // ==========================================
    // 1) GET ALL PERMISSIONS FOR ROLE
    // ==========================================
    @Override
    public RolePermissionsResponse getPermissionsForRole(Long roleId) {

        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<ModuleEntity> modules = moduleRepo.findAll();
        List<Permission> allPermissions = permissionRepo.findAllByOrderByModuleIdAscIdAsc();
        List<RolePermission> rolePermissions = rolePermissionRepo.findByRoleId(roleId);

        Map<Long, Boolean> allowedMap = new HashMap<>();
        for (RolePermission rp : rolePermissions) {
            allowedMap.put(rp.getPermissionId(), rp.isAllowed());
        }

        RolePermissionsResponse response = new RolePermissionsResponse();
        response.setRoleId(roleId);
        response.setRoleName(role.getName());
        response.setSubTitle(role.getSubTitle());

        List<RolePermissionsResponse.ModuleDTO> moduleDTOList = new ArrayList<>();

        for (ModuleEntity mod : modules) {

            RolePermissionsResponse.ModuleDTO moduleDTO = new RolePermissionsResponse.ModuleDTO();
            moduleDTO.setModuleId(mod.getId());
            moduleDTO.setModuleName(mod.getName());

            List<RolePermissionsResponse.PermissionDTO> perms = new ArrayList<>();

            for (Permission p : allPermissions) {
                if (p.getModuleId().equals(mod.getId())) {

                    RolePermissionsResponse.PermissionDTO dto = new RolePermissionsResponse.PermissionDTO();
                    dto.setPermissionId(p.getId());
                    dto.setLabel(p.getLabel());
                    dto.setAllowed(allowedMap.getOrDefault(p.getId(), false));

                    perms.add(dto);
                }
            }

            moduleDTO.setPermissions(perms);
            moduleDTOList.add(moduleDTO);
        }

        response.setModules(moduleDTOList);
        return response;
    }

    // ==========================================
    // 2) SAVE ROLE PERMISSIONS
    // ==========================================
    @Override
    @Transactional
    public void saveRolePermissions(Long roleId, RolePermissionsSaveRequest request) {

        // DELETE old data
        rolePermissionRepo.deleteByRoleId(roleId);

        // FLUSH to force delete BEFORE inserts
        rolePermissionRepo.flush();

        // INSERT fresh data
        for (RolePermissionsSaveRequest.PermissionEntry entry : request.getPermissions()) {

            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setModuleId(entry.getModuleId());
            rp.setPermissionId(entry.getPermissionId());
            rp.setAllowed(entry.isAllowed());

            rolePermissionRepo.save(rp);
        }
    }


    // ==========================================
    // 3) RESET ALL PERMISSIONS TO TRUE
    // ==========================================
    @Override
    @Transactional
    public void resetPermissions(Long roleId) {

        // 1. DELETE old permissions
        rolePermissionRepo.deleteByRoleId(roleId);

        // 2. FORCE delete BEFORE insert
        rolePermissionRepo.flush();

        // 3. INSERT default permissions
        List<Permission> all = permissionRepo.findAll();

        for (Permission p : all) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setModuleId(p.getModuleId());
            rp.setPermissionId(p.getId());
            rp.setAllowed(false);
            rolePermissionRepo.save(rp);
        }
    }

}
