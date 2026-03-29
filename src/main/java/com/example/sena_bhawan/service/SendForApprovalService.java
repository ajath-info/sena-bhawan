package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.SendForApprovalRequest;
import com.example.sena_bhawan.dto.SendForApprovalResponse;
import com.example.sena_bhawan.entity.CoursePanelBatch;
import com.example.sena_bhawan.entity.CoursePanelNomination;
import com.example.sena_bhawan.repository.CoursePanelBatchRepository;
import com.example.sena_bhawan.repository.CoursePanelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SendForApprovalService {

    private final CoursePanelBatchRepository batchRepository;
    private final CoursePanelRepository nominationRepository;

    private static final Long FIXED_MOVEMENT_ID = 2L;

    public SendForApprovalResponse sendForApproval(SendForApprovalRequest request) {

        List<CoursePanelNomination> existingNominations =
                nominationRepository.findByScheduleId(request.getScheduleId());

        if (existingNominations.isEmpty()) {
            throw new IllegalStateException(
                    "No nominations found for scheduleId: " + request.getScheduleId()
                            + ". Please save the panel in Step 3 first.");
        }

        // 3. Deactivate all previous batches for this schedule
        deactivatePreviousBatches(request.getScheduleId());

        // 4. Create new batch with batch_status = true
        CoursePanelBatch batch = CoursePanelBatch.builder()
                .scheduleId(request.getScheduleId())
                .movementId(FIXED_MOVEMENT_ID)
                .status("PENDING_APPROVAL")
                .batchStatus(true) // New batch is active
                .totalNominations(request.getNominations().size())
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        batch = batchRepository.save(batch);
        final Long batchId = batch.getId();

        // 5. Build serial number map
        Map<Long, Long> serialMap = request.getNominations().stream()
                .filter(item -> item.getSerialNumber() != null)
                .collect(Collectors.toMap(
                        SendForApprovalRequest.NominationSerialItem::getPersonnelId,
                        SendForApprovalRequest.NominationSerialItem::getSerialNumber,
                        (a, b) -> a
                ));

        // 6. Update nominations with new batchId
        int updatedCount = 0;

        for (CoursePanelNomination nomination : existingNominations) {
            Long pId = nomination.getPersonnelId();

            // Update batchId on every nomination for this schedule
            nomination.setBatchId(batchId);

            // Set nomination status to PENDING_APPROVAL
            nomination.setStatus("PENDING_APPROVAL");

            // Update serial number if provided
            if (serialMap.containsKey(pId)) {
                nomination.setSerialNumber(serialMap.get(pId));
            }

            nomination.setUpdatedAt(LocalDateTime.now());
            nominationRepository.save(nomination);
            updatedCount++;
        }

        // 7. Update total nominations count
        batch.setTotalNominations(updatedCount);
        batchRepository.save(batch);

        return SendForApprovalResponse.builder()
                .batchId(batchId)
                .scheduleId(request.getScheduleId())
                .movementId(FIXED_MOVEMENT_ID)
                .status("PENDING_APPROVAL")
                .totalNominations(updatedCount)
                .message("Panel submitted for approval. Batch #" + batchId + " created with "
                        + updatedCount + " nominations.")
                .build();
    }

    /**
     * Deactivates all previous batches for a given schedule
     * Sets batch_status = false for all existing batches
     */
    private void deactivatePreviousBatches(Long scheduleId) {
        List<CoursePanelBatch> existingBatches = batchRepository.findByScheduleId(scheduleId);

        if (!existingBatches.isEmpty()) {
            for (CoursePanelBatch batch : existingBatches) {
                // Only update if it's currently active
                if (Boolean.TRUE.equals(batch.getBatchStatus())) {
                    batch.setBatchStatus(false);
                    batch.setUpdatedAt(LocalDateTime.now());
                    batchRepository.save(batch);
                }
            }
        }
    }
}