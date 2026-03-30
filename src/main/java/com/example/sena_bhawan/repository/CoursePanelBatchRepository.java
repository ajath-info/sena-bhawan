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

    // New methods for role-wise filtering
    Page<CoursePanelBatch> findByMovementIdAndStatusIn(
            Long movementId,
            List<String> statuses,
            Pageable pageable);

    @Query("SELECT cpb FROM CoursePanelBatch cpb " +
            "WHERE cpb.movementId + 1 = :movementId " +
            "AND cpb.status = 'PENDING_APPROVAL' " +
            "AND cpb.batchStatus = true " +
            "ORDER BY cpb.createdAt DESC")
    Page<CoursePanelBatch> findByMovementIdLessThanEqualAndStatusPending(
            @Param("movementId") Long movementId,
            Pageable pageable);

    @Query("SELECT cpb FROM CoursePanelBatch cpb " +
            "WHERE cpb.movementId >= :movementId " +
            "AND (cpb.status = 'APPROVED' OR cpb.status = 'PENDING_APPROVAL') " +
            "AND cpb.batchStatus = true " +
            "ORDER BY cpb.createdAt DESC")
    Page<CoursePanelBatch> findByMovementIdLessThanEqualAndStatusApprove(
            @Param("movementId") Long movementId,
            Pageable pageable);

    @Query("SELECT cpb FROM CoursePanelBatch cpb " +
            "WHERE cpb.movementId >= :movementId " +
            "AND cpb.status = 'REJECTED' " +
            "AND cpb.batchStatus = true " +
            "ORDER BY cpb.createdAt DESC")
    Page<CoursePanelBatch> findByMovementIdLessThanEqualAndStatusRejected(
            @Param("movementId") Long movementId,
            Pageable pageable);

    @Query("SELECT COUNT(cpb) FROM CoursePanelBatch cpb " +
            "WHERE cpb.movementId <= :movementId " +
            "AND cpb.batchStatus = true " +
            "AND cpb.status = :status")
    long countByMovementIdLessThanEqualAndStatus(
            @Param("movementId") Long movementId,
            @Param("status") String status);

    @Query("SELECT cpb FROM CoursePanelBatch cpb " +
            "WHERE cpb.batchStatus = true " +
            "ORDER BY cpb.createdAt DESC")
    Page<CoursePanelBatch> findAllActiveBatches(Pageable pageable);

    // Fetch all batches by status (no movement filter)
    @Query("SELECT cpb FROM CoursePanelBatch cpb " +
            "WHERE cpb.status = :status " +
            "AND cpb.batchStatus = true " +
            "ORDER BY cpb.createdAt DESC")
    Page<CoursePanelBatch> findByStatus(
            @Param("status") String status,
            Pageable pageable);

}
