package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.MedicalCategoryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalCategoryMasterRepository
        extends JpaRepository<MedicalCategoryMaster, Long> {

    @Query("""
        SELECT m.medicalName
        FROM MedicalCategoryMaster m  
        WHERE m.isActive = true
        ORDER BY m.medicalName
    """)
    List<String> findActiveMedicalCategories();
}
