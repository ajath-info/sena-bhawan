package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(Long roleId);

    @Modifying
    @Transactional
    void deleteByRoleId(Long roleId);
}
