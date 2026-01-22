package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.repository.UserPermissionRepository;
import com.example.sena_bhawan.entity.UserMaster;
import com.example.sena_bhawan.repository.UserMasterRepository;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserPermissionService {

    private final UserPermissionRepository repo;
    private final UserMasterRepository userRepo;

    public UserPermissionService(UserPermissionRepository repo, UserMasterRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public UserPermissionsResponse getUserPermissions(Long userId) {

        List<UserPermissionProjection> rows = repo.getPermissionsForUser(userId);

        Map<Long, ModuleResponse> moduleMap = new LinkedHashMap<>();

        for (UserPermissionProjection row : rows) {

            moduleMap.putIfAbsent(row.getModuleId(),
                    new ModuleResponse(row.getModuleId(), row.getModuleName(), new ArrayList<>()));

            moduleMap.get(row.getModuleId()).getPermissions()
                    .add(new PermissionResponse(
                            row.getPermissionId(),
                            row.getLabel(),
                            row.getAllowed(),
                            row.getUrl()
                    ));
        }

        UserMaster user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserPermissionsResponse(
                user.getUserId(),
                user.getUsername(),
                new ArrayList<>(moduleMap.values())
        );
    }
}

