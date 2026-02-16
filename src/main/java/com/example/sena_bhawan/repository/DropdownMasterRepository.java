package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.DropdownMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DropdownMasterRepository extends JpaRepository<DropdownMaster,Long> {
    List<DropdownMaster> findByTypeAndStatus(String type, Integer status);

}
