package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.RoleMaster;
import com.example.sena_bhawan.repository.RoleMasterRepository;
import com.example.sena_bhawan.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMasterRepository roleRepo;

    public RoleServiceImpl(RoleMasterRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public List<RoleMaster> getAllRoles() {
        return roleRepo.findAll();
    }
}

