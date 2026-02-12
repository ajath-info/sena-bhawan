package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.Permission;
import com.example.sena_bhawan.repository.PermissionRepository;
import com.example.sena_bhawan.service.PermissionService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository repo;

    public PermissionServiceImpl(PermissionRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Permission> getAllPermissions() {
        return repo.findAll();
    }

    @Override
    public Permission getPermissionById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
    }

    @Override
    public Permission addPermission(Permission permission) {
        return repo.save(permission);
    }

    @Override
    public Permission updatePermission(Long id, Permission permission) {
        Permission existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));

        existing.setModuleId(permission.getModuleId());
        existing.setCode(permission.getCode());
        existing.setLabel(permission.getLabel());
        existing.setUrl(permission.getUrl());

        return repo.save(existing);
    }

    @Override
    public void deletePermission(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Permission not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
