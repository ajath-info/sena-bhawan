//package com.example.sena_bhawan.service.impl;
//
//import com.example.sena_bhawan.repository.CoursePanelBatchRepository;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class BatchApprovalService {
//
//    private final CoursePanelBatchRepository batchRepository;
//    private final CoursePanelNominationRepository nominationRepository;
//    private final CoursePanelBatchHistoryService historyService;
//
//    @Transactional
//    public BatchApprovalResponse approveBatch(Long batchId, BatchApprovalRequest request) {
//        CoursePanelBatch batch = batchRepository.findById(batchId)
//                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + batchId));
//
//        // Validate current status
//        if (!"PENDING_APPROVAL".equals(batch.getStatus())) {
//            throw new IllegalStateException("Batch is not in PENDING_APPROVAL status. Current status: " + batch.getStatus());
//        }
//
//        // Update batch
//        batch.setStatus("APPROVED");
//        batch.setBatchStatus(true); // Keep active
//        batch.setUpdatedAt(LocalDateTime.now());
//        if (request.getRemarks() != null) {
//            batch.setRemarks(request.getRemarks());
//        }
//        batchRepository.save(batch);
//
//        // Update all nominations in this batch
//        List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batchId);
//        for (CoursePanelNomination nomination : nominations) {
//            nomination.setStatus("CONFIRMED");
//            nomination.setUpdatedAt(LocalDateTime.now());
//            nominationRepository.save(nomination);
//        }
//
//        // Create history record
//        historyService.createBatchHistory(batch, "APPROVED", request.getApproverId(), request.getRemarks());
//
//        return BatchApprovalResponse.builder()
//                .batchId(batchId)
//                .status("APPROVED")
//                .message("Batch #" + batchId + " has been approved successfully")
//                .build();
//    }
//
//    @Transactional
//    public BatchApprovalResponse rejectBatch(Long batchId, BatchRejectionRequest request) {
//        CoursePanelBatch batch = batchRepository.findById(batchId)
//                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + batchId));
//
//        // Validate current status
//        if (!"PENDING_APPROVAL".equals(batch.getStatus())) {
//            throw new IllegalStateException("Cannot reject batch. Current status: " + batch.getStatus());
//        }
//
//        // Find the latest batch to get next movement ID
//        List<CoursePanelBatch> existingBatches = batchRepository.findByScheduleIdOrderByCreatedAtDesc(batch.getScheduleId());
//        Long nextMovementId = existingBatches.isEmpty() ? 1L :
//                existingBatches.get(0).getMovementId() + 1;
//
//        // Update current batch as rejected
//        batch.setStatus("REJECTED");
//        batch.setBatchStatus(false); // Inactive
//        batch.setRejectMovementId(request.getRejectMovementId()); // Track rejection movement ID
//        batch.setUpdatedAt(LocalDateTime.now());
//        if (request.getRemarks() != null) {
//            batch.setRemarks(request.getRemarks());
//        }
//        batchRepository.save(batch);
//
//        // Update nominations status
//        List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batchId);
//        for (CoursePanelNomination nomination : nominations) {
//            nomination.setStatus("REJECTED");
//            nomination.setUpdatedAt(LocalDateTime.now());
//            nominationRepository.save(nomination);
//        }
//
//        // Create history record
//        historyService.createBatchHistory(batch, "REJECTED", request.getRejecterId(), request.getRemarks());
//
//        return BatchApprovalResponse.builder()
//                .batchId(batchId)
//                .status("REJECTED")
//                .message("Batch #" + batchId + " has been rejected. You can create a new batch.")
//                .nextMovementId(nextMovementId)
//                .build();
//    }
//
//    @Transactional
//    public BatchApprovalResponse pendingBatch(Long batchId, BatchPendingRequest request) {
//        CoursePanelBatch batch = batchRepository.findById(batchId)
//                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + batchId));
//
//        // If batch is already pending, just update remarks
//        if ("PENDING_APPROVAL".equals(batch.getStatus())) {
//            if (request.getRemarks() != null) {
//                batch.setRemarks(request.getRemarks());
//                batch.setUpdatedAt(LocalDateTime.now());
//                batchRepository.save(batch);
//
//                historyService.createBatchHistory(batch, "PENDING_UPDATED", request.getUserId(), request.getRemarks());
//            }
//
//            return BatchApprovalResponse.builder()
//                    .batchId(batchId)
//                    .status("PENDING_APPROVAL")
//                    .message("Batch #" + batchId + " remarks updated")
//                    .build();
//        }
//
//        throw new IllegalStateException("Batch is not in PENDING_APPROVAL status. Current status: " + batch.getStatus());
//    }
//}