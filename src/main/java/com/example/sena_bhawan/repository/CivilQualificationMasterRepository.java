package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CivilQualificationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CivilQualificationMasterRepository
        extends JpaRepository<CivilQualificationMaster, Long> {

    @Query("""
        SELECT c.qualificationName
        FROM CivilQualification c
        WHERE c.isActive = true
        ORDER BY c.qualificationName
    """)
    List<String> findActiveQualifications();
}
