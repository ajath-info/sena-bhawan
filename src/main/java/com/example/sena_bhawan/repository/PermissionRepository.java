package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findAllByOrderByModuleIdAscIdAsc();
}
