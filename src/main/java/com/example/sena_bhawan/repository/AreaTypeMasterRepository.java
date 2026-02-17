package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.AreaTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaTypeMasterRepository
        extends JpaRepository<AreaTypeMaster, Long> {

    @Query("""
        SELECT a.areaName
        FROM AreaType a
        WHERE a.isActive = true
        ORDER BY a.areaName
    """)
    List<String> findActiveAreaTypes();
}
