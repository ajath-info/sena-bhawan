package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.BatchActionRequest;
import com.example.sena_bhawan.dto.BatchActionResponse;
import com.example.sena_bhawan.entity.CoursePanelBatch;
import com.example.sena_bhawan.entity.CoursePanelNomination;
import com.example.sena_bhawan.repository.CoursePanelBatchRepository;
import com.example.sena_bhawan.repository.CoursePanelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BatchActionService {

    private final CoursePanelBatchRepository batchRepository;
    private final CoursePanelRepository nominationRepository;
    
    private static final Long FINAL_MOVEMENT_ID = 5L; // Movement ID where final approval happens
    private static final Long MIN_MOVEMENT_ID = 2L; // Minimum movement ID for send back

    /**
     * Approve a batch
     * - If movementId = 4: Status becomes APPROVED
     * - If movementId < 4: MovementId increments by 1, status remains PENDING_APPROVAL
     */
    public BatchActionResponse approveBatch(Long batchId, BatchActionRequest request) {
        CoursePanelBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with ID: " + batchId));

        // Validate batch can be approved
        if (!"PENDING_APPROVAL".equals(batch.getStatus())) {
            throw new IllegalStateException("Batch cannot be approved. Current status: " + batch.getStatus());
        }

        String message;
        Long originalMovementId = batch.getMovementId();

        // Always increment movementId by 1
        Long newMovementId = batch.getMovementId() + 1;
        batch.setMovementId(newMovementId);

        // Check if this is the final movement (movementId was 4)
        if (FINAL_MOVEMENT_ID.equals(originalMovementId)) {
            // Final approval after increment
            batch.setStatus("APPROVED");
            batch.setBatchStatus(true);
            message = "Batch approved successfully (final approval)";

            // Update all nominations to CONFIRMED
            List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batchId);
            for (CoursePanelNomination nomination : nominations) {
                nomination.setStatus("CONFIRMED");
                nomination.setUpdatedAt(LocalDateTime.now());
                nominationRepository.save(nomination);
            }

            log.info("Batch {} approved (final approval) - moved from movement {} to {}",
                    batchId, originalMovementId, newMovementId);
        } else {
            // Not final approval - keep PENDING_APPROVAL
            batch.setStatus("PENDING_APPROVAL");
            batch.setBatchStatus(true);
            message = String.format("Batch forwarded to next level (Movement %d -> %d)",
                    originalMovementId, newMovementId);

            log.info("Batch {} forwarded from movement {} to {}",
                    batchId, originalMovementId, newMovementId);
        }

        if (request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) {
            Long roleId = request.getRoleId();
            String remarkText = request.getRemarks();

            if (roleId != null) {
                switch (roleId.intValue()) {
                    case 2:
                        batch.setRemark2(remarkText);
                        log.info("Stored remark in remark2 for roleId: {}", roleId);
                        break;
                    case 3:
                        batch.setRemark3(remarkText);
                        log.info("Stored remark in remark3 for roleId: {}", roleId);
                        break;
                    case 4:
                        batch.setRemark4(remarkText);
                        log.info("Stored remark in remark4 for roleId: {}", roleId);
                        break;
                    case 5:
                        batch.setRemark5(remarkText);
                        log.info("Stored remark in remark5 for roleId: {}", roleId);
                        break;
                    case 6:
                        batch.setRemark6(remarkText);
                        log.info("Stored remark in remark6 for roleId: {}", roleId);
                        break;
                    default:
                        throw new RuntimeException("Invalid request");
                }
            } else {
                // If roleId is null, store in generic remarks
                batch.setRemarks(remarkText);
                log.warn("roleId is null, storing remark in generic remarks column");
            }
        }

        batch.setUpdatedAt(LocalDateTime.now());
        batchRepository.save(batch);

        return BatchActionResponse.builder()
                .batchId(batch.getId())
                .scheduleId(batch.getScheduleId())
                .movementId(batch.getMovementId())
                .status(batch.getStatus())
                .message(message)
                .build();
    }

    /**
     * Reject a batch
     * - Status becomes REJECTED
     * - rejectMovementId stores the movement ID where rejection happened
     * - batchStatus becomes false
     */
    public BatchActionResponse rejectBatch(Long batchId, BatchActionRequest request) {
        CoursePanelBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with ID: " + batchId));

        // Validate batch can be rejected
        if (!"PENDING_APPROVAL".equals(batch.getStatus())) {
            throw new IllegalStateException("Batch cannot be rejected. Current status: " + batch.getStatus());
        }

        // Update batch
        batch.setStatus("REJECTED");
        batch.setMovementId(request.getRejectMovementId()+1);
        batch.setRejectMovementId(request.getRejectMovementId() != null ?
                request.getRejectMovementId()+1 : batch.getMovementId()+1);

        if (request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) {
            Long roleId = request.getRoleId();
            String remarkText = request.getRemarks();

            if (roleId != null) {
                switch (roleId.intValue()) {
                    case 2:
                        batch.setRemark2(remarkText);
                        log.info("Stored remark in remark2 for roleId: {} (Rejection)", roleId);
                        break;
                    case 3:
                        batch.setRemark3(remarkText);
                        log.info("Stored remark in remark3 for roleId: {} (Rejection)", roleId);
                        break;
                    case 4:
                        batch.setRemark4(remarkText);
                        log.info("Stored remark in remark4 for roleId: {} (Rejection)", roleId);
                        break;
                    case 5:
                        batch.setRemark5(remarkText);
                        log.info("Stored remark in remark5 for roleId: {} (Rejection)", roleId);
                        break;
                    case 6:
                        batch.setRemark6(remarkText);
                        log.info("Stored remark in remark6 for roleId: {} (Rejection)", roleId);
                        break;
                    default:
                        throw new RuntimeException("Invalid request: Unsupported roleId");
                }
            } else {
                throw new RuntimeException("Invalid request: roleId is required for rejection");
            }
        }

        batch.setUpdatedAt(LocalDateTime.now());
        batchRepository.save(batch);

        // Update all nominations to REJECTED
        List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batchId);
        for (CoursePanelNomination nomination : nominations) {
            nomination.setStatus("REJECTED");
            nomination.setUpdatedAt(LocalDateTime.now());
            nominationRepository.save(nomination);
        }

        log.info("Batch {} rejected at movement ID: {}", batchId, batch.getRejectMovementId());

        return BatchActionResponse.builder()
                .batchId(batch.getId())
                .scheduleId(batch.getScheduleId())
                .movementId(batch.getMovementId())
                .status("REJECTED")
                .rejectMovementId(batch.getRejectMovementId())
                .message("Batch rejected successfully")
                .build();
    }

    /**
     * Send back a batch
     * - Status remains PENDING_APPROVAL
     * - Movement ID decreases by 1 (but not less than MIN_MOVEMENT_ID)
     */
    public BatchActionResponse sendBackBatch(Long batchId, BatchActionRequest request) {
        CoursePanelBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with ID: " + batchId));

        // Validate batch can be sent back
        if (!"PENDING_APPROVAL".equals(batch.getStatus())) {
            throw new IllegalStateException("Batch cannot be sent back. Current status: " + batch.getStatus());
        }

        // Validate remarks are provided for send back
        if (request.getRemarks() == null || request.getRemarks().trim().isEmpty()) {
            throw new IllegalArgumentException("Remarks are required when sending back a batch");
        }

        // Decrease movement ID but not below MIN_MOVEMENT_ID (2)
        Long currentMovementId = batch.getMovementId();
        Long newMovementId = currentMovementId - 1;

        if (newMovementId < MIN_MOVEMENT_ID) {
            throw new IllegalStateException(
                    String.format("Cannot send back batch below movement ID %d. Current movement ID: %d",
                            MIN_MOVEMENT_ID, currentMovementId));
        }

        // Update batch
        batch.setMovementId(newMovementId);
        batch.setStatus("PENDING_APPROVAL");
        batch.setBatchStatus(true); // Keep active

        if (request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) {
            Long roleId = request.getRoleId();
            String remarkText = request.getRemarks();

            if (roleId != null) {
                switch (roleId.intValue()) {
                    case 2:
                        batch.setRemark2(remarkText);
                        log.info("Stored remark in remark2 for roleId: {} (Send Back)", roleId);
                        break;
                    case 3:
                        batch.setRemark3(remarkText);
                        log.info("Stored remark in remark3 for roleId: {} (Send Back)", roleId);
                        break;
                    case 4:
                        batch.setRemark4(remarkText);
                        log.info("Stored remark in remark4 for roleId: {} (Send Back)", roleId);
                        break;
                    case 5:
                        batch.setRemark5(remarkText);
                        log.info("Stored remark in remark5 for roleId: {} (Send Back)", roleId);
                        break;
                    case 6:
                        batch.setRemark6(remarkText);
                        log.info("Stored remark in remark6 for roleId: {} (Send Back)", roleId);
                        break;
                    default:
                        throw new RuntimeException("Invalid request: Unsupported roleId");
                }
            } else {
                throw new RuntimeException("Invalid request: roleId is required for send back");
            }
        }

        batch.setUpdatedAt(LocalDateTime.now());
        batchRepository.save(batch);

        // Update nominations status if needed (keep them as PENDING_APPROVAL)
        List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batchId);
        for (CoursePanelNomination nomination : nominations) {
            nomination.setStatus("PENDING_APPROVAL");
            nomination.setUpdatedAt(LocalDateTime.now());
            nominationRepository.save(nomination);
        }

        log.info("Batch {} sent back from movement {} to movement {}",
                batchId, currentMovementId, newMovementId);

        return BatchActionResponse.builder()
                .batchId(batch.getId())
                .scheduleId(batch.getScheduleId())
                .movementId(batch.getMovementId())
                .status("PENDING_APPROVAL")
                .message(String.format("Batch sent back from movement %d to %d",
                        currentMovementId, newMovementId))
                .build();
    }
}