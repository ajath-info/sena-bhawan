package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.Permission;
import com.example.sena_bhawan.service.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    @Autowired(required = true)
    PermissionService service;

    @GetMapping
    public List<Permission> getAllPermissions() {
        return service.getAllPermissions();
    }

    @GetMapping("/{id}")
    public Permission getPermissionById(@PathVariable Long id) {
        return service.getPermissionById(id);
    }

    @PostMapping
    public Permission addPermission(@RequestBody Permission permission) {
        return service.addPermission(permission);
    }

    @PutMapping("/{id}")
    public Permission updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        return service.updatePermission(id, permission);
    }

    @DeleteMapping("/{id}")
    public String deletePermission(@PathVariable Long id) {
        service.deletePermission(id);
        return "Permission deleted successfully!";
    }
}
