package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.Role;
import com.example.sena_bhawan.repository.RoleRepository;
import com.example.sena_bhawan.service.RoleCrud;
import com.example.sena_bhawan.service.RoleService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class RoleCrudImpl implements RoleCrud {

    @Autowired
    private RoleRepository repo;


    @Override
    public Role getRoleById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    @Override
    public Role addRole(Role role) {
        return repo.save(role);
    }

    @Override
    public Role updateRole(Long id, Role role) {
        Role existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        existing.setName(role.getName());
        existing.setSubTitle(role.getSubTitle());

        return repo.save(existing);
    }

    @Override
    public void deleteRole(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
