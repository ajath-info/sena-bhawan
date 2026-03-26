package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.RoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, Long> {
    List<RoleMaster> findAllByOrderByHierarchyOrderDesc();
}

