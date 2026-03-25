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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SendForApprovalService {

    private final CoursePanelBatchRepository batchRepository;
    private final CoursePanelRepository nominationRepository;

    private static final Long FIXED_MOVEMENT_ID = 2L;

    public SendForApprovalResponse sendForApproval(SendForApprovalRequest request) {

        // 1. Count existing nominations for this schedule to confirm they exist
        List<CoursePanelNomination> existingNominations =
                nominationRepository.findByScheduleId(request.getScheduleId());

        if (existingNominations.isEmpty()) {
            throw new IllegalStateException(
                "No nominations found for scheduleId: " + request.getScheduleId()
                + ". Please save the panel in Step 3 first.");
        }

        // 2. Create the batch with movementId always = 1
        CoursePanelBatch batch = CoursePanelBatch.builder()
                .scheduleId(request.getScheduleId())
                .movementId(FIXED_MOVEMENT_ID)
                .status("PENDING_APPROVAL")
                .totalNominations(request.getNominations().size())
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        batch = batchRepository.save(batch);
        final Long batchId = batch.getId();

        // 3. Update each nomination: set serialNumber + batchId
        //    Build a quick lookup map: personnelId -> serialNumber from request
        Map<Long, Long> serialMap = request.getNominations().stream()
                .filter(item -> item.getSerialNumber() != null)
                .collect(Collectors.toMap(
                        SendForApprovalRequest.NominationSerialItem::getPersonnelId,
                        SendForApprovalRequest.NominationSerialItem::getSerialNumber,
                        (a, b) -> a // keep first if duplicate personnelId
                ));

        int updatedCount = 0;
        List<String> warnings = new ArrayList<>();

        for (CoursePanelNomination nomination : existingNominations) {
            Long pId = nomination.getPersonnelId();

            // Update batchId on every nomination for this schedule
            nomination.setBatchId(batchId);

            // Update serial number only if provided in request
            if (serialMap.containsKey(pId)) {
                nomination.setSerialNumber(serialMap.get(pId));
            }

            nomination.setUpdatedAt(LocalDateTime.now());
            nominationRepository.save(nomination);
            updatedCount++;
        }

        // 4. Update totalNominations with actual count
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
}