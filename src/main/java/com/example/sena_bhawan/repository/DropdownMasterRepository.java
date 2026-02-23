package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.DropdownMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DropdownMasterRepository extends JpaRepository<DropdownMaster,Long> {
    List<DropdownMaster> findByTypeAndStatus(String type, Integer status);
    List<DropdownMaster> findByTypeIgnoreCaseAndStatus(String type, Integer status);
    Optional<DropdownMaster> findByTypeAndName(String type, String name);
    Optional<DropdownMaster> findById(Long id);
}
