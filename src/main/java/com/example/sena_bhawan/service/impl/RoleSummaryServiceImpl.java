package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.RoleSummaryDto;
import com.example.sena_bhawan.entity.Role;
import com.example.sena_bhawan.repository.RolePermissionRepository;
import com.example.sena_bhawan.repository.RoleRepository;
import com.example.sena_bhawan.service.RoleSummaryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RoleSummaryServiceImpl implements RoleSummaryService {

    private final RoleRepository roleRepo;
    private final RolePermissionRepository rolePermissionRepo;

    public RoleSummaryServiceImpl(RoleRepository roleRepo,
                                  RolePermissionRepository rolePermissionRepo) {
        this.roleRepo = roleRepo;
        this.rolePermissionRepo = rolePermissionRepo;
    }

    @Override
    public List<RoleSummaryDto> getRoleSummaries() {
        List<Role> roles = roleRepo.findAll();
        List<RoleSummaryDto> result = new ArrayList<>();
        Random random = new Random();

        for (Role role : roles) {

            int allowedCount =
                    rolePermissionRepo.findByRoleId(role.getId())
                            .stream()
                            .mapToInt(rp -> rp.isAllowed() ? 1 : 0)
                            .sum();

            RoleSummaryDto dto = new RoleSummaryDto();
            dto.setRoleId(role.getId());
            dto.setRoleName(role.getName());
            dto.setSubTitle(role.getSubTitle());
            dto.setUserCount(10 + random.nextInt(90));   // random 10-100
            dto.setAllowedPermissionCount(allowedCount);

            result.add(dto);
        }

        return result;
    }
}
