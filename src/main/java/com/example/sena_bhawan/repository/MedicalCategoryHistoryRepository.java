package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.MedicalCategoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalCategoryHistoryRepository
        extends JpaRepository<MedicalCategoryHistory, Long> {

    List<MedicalCategoryHistory> findByPersonnelId(Long personnelId);
}
