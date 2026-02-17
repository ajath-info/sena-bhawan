package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByModuleId(Long moduleId);
    List<Permission> findByCode(String code);
    List<Permission> findAllByOrderByModuleIdAscIdAsc();
}
