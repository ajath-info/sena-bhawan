package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CoursePanelBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoursePanelBatchRepository extends JpaRepository<CoursePanelBatch, Long> {
    List<CoursePanelBatch> findByScheduleId(Long scheduleId);
    Optional<CoursePanelBatch> findFirstByScheduleIdOrderByCreatedAtDesc(Long scheduleId);
}
