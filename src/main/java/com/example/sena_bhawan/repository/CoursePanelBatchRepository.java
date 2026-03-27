package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CoursePanelBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoursePanelBatchRepository extends JpaRepository<CoursePanelBatch, Long> {
    List<CoursePanelBatch> findByScheduleId(Long scheduleId);
    Optional<CoursePanelBatch> findFirstByScheduleIdOrderByCreatedAtDesc(Long scheduleId);


    List<CoursePanelBatch> findByScheduleIdOrderByCreatedAtDesc(Long scheduleId);


    List<CoursePanelBatch> findByScheduleIdAndBatchStatusTrue(Long scheduleId);

    List<CoursePanelBatch> findByScheduleIdAndStatus(Long scheduleId, String status);

    // New methods for role-wise filtering
    Page<CoursePanelBatch> findByMovementIdAndStatusIn(
            Long movementId,
            List<String> statuses,
            Pageable pageable);

    Page<CoursePanelBatch> findByMovementIdAndStatusInAndBatchStatus(
            Long movementId,
            List<String> statuses,
            Boolean batchStatus,
            Pageable pageable);

    @Query("SELECT b FROM CoursePanelBatch b WHERE b.movementId = :movementId AND b.status IN :statuses ORDER BY b.createdAt DESC")
    Page<CoursePanelBatch> findPanelsByMovementAndStatuses(
            @Param("movementId") Long movementId,
            @Param("statuses") List<String> statuses,
            Pageable pageable);
}
