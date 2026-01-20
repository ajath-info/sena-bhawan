package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.RolePermissionsResponse;
import com.example.sena_bhawan.dto.RolePermissionsSaveRequest;
import com.example.sena_bhawan.dto.RoleSummaryDto;
import com.example.sena_bhawan.service.RolePermissionService;
import com.example.sena_bhawan.service.RoleSummaryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final RoleSummaryService roleSummaryService;

    // ----------------------------------------------------
    // 🔹 1. GET ALL PERMISSIONS FOR A ROLE
    // ----------------------------------------------------
    @GetMapping("/{roleId}/permissions")
    public RolePermissionsResponse getPermissions(@PathVariable Long roleId) {
        return rolePermissionService.getPermissionsForRole(roleId);
    }

    // ----------------------------------------------------
    // 🔹 2. SAVE ALL PERMISSIONS FOR ROLE
    // ----------------------------------------------------
    @PostMapping("/{roleId}/permissions/save")
    public ResponseEntity<String> savePermissions(
            @PathVariable Long roleId,
            @RequestBody RolePermissionsSaveRequest request) {

        rolePermissionService.saveRolePermissions(roleId, request);
        return ResponseEntity.ok("Permissions saved successfully.");
    }

    // ----------------------------------------------------
    // 🔹 3. RESET ALL PERMISSIONS TO DEFAULT (all true)
    // ----------------------------------------------------
    @PostMapping("/{roleId}/reset")
    public ResponseEntity<String> resetToDefault(@PathVariable Long roleId) {

        rolePermissionService.resetPermissions(roleId);
        return ResponseEntity.ok("Permissions reset to default.");
    }

    // ----------------------------------------------------
    // 🔹 4. ROLE SUMMARY → For role cards on UI
    // ----------------------------------------------------
    @GetMapping("/summary")
    public List<RoleSummaryDto> getRoleSummaries() {
        return roleSummaryService.getRoleSummaries();
    }
}
