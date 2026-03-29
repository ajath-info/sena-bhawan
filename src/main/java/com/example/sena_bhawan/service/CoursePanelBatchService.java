package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CoursePanelBatchService {

    private final CoursePanelBatchRepository batchRepository;
    private final CoursePanelRepository nominationRepository;
    private final CourseScheduleRepository scheduleRepository;
    private final CourseMasterRepository courseMasterRepository;
    private final PersonnelRepository personnelRepository;

    /**
     * Create a new batch and persist all nominations linked to it.
     */
    public CoursePanelBatchResponse createBatch(CreateCoursePanelBatchRequest request) {
        // 1. Build and save the batch first so we get its generated ID
        CoursePanelBatch batch = CoursePanelBatch.builder()
                .scheduleId(request.getScheduleId())
                .movementId(request.getMovementId())
                .status("DRAFT")
                .totalNominations(request.getNominations().size())
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        batch = batchRepository.save(batch);
        final Long batchId = batch.getId();

        // 2. Save each nomination, referencing the new batchId
        List<CoursePanelNomination> saved = request.getNominations().stream()
                .map(item -> CoursePanelNomination.builder()
                        .scheduleId(request.getScheduleId())
                        .personnelId(item.getPersonnelId())
                        .attendanceStatus(
                                item.getAttendanceStatus() != null ? item.getAttendanceStatus() : "Reserve")
                        .serialNumber(item.getSerialNumber())
                        .status("ACTIVE")
                        .batchId(batchId)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build())
                .map(nominationRepository::save)
                .collect(Collectors.toList());

        // 3. Update totalNominations with actual saved count
        batch.setTotalNominations(saved.size());
        batchRepository.save(batch);

        return buildResponse(batch, saved, "Batch created successfully");
    }

    /**
     * Update an existing batch — modify its status/remarks and
     * update any of its nominations (serial number, attendance status, etc.).
     */
    public CoursePanelBatchResponse updateBatch(Long batchId,
                                                 UpdateCoursePanelBatchRequest request) {
        CoursePanelBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Batch not found with id: " + batchId));

        // Update batch-level fields if provided
        if (request.getStatus() != null) {
            batch.setStatus(request.getStatus());
        }
        if (request.getRemarks() != null) {
            batch.setRemarks(request.getRemarks());
        }
        batch.setUpdatedAt(LocalDateTime.now());
        batchRepository.save(batch);

        List<String> errors = new ArrayList<>();

        // Update each nomination
        if (request.getNominations() != null) {
            for (UpdateCoursePanelBatchRequest.NominationUpdate update : request.getNominations()) {
                try {
                    updateSingleNomination(batchId, update, errors);
                } catch (Exception e) {
                    errors.add("Error updating nomination: " + e.getMessage());
                }
            }
        }

        if (!errors.isEmpty()) {
            // Still return the updated state but signal partial failure
            List<CoursePanelNomination> current = nominationRepository.findByBatchId(batchId);
            CoursePanelBatchResponse response = buildResponse(batch, current, "Partial update completed");
            // You can add an errors field to CoursePanelBatchResponse if needed
            return response;
        }

        List<CoursePanelNomination> updated = nominationRepository.findByBatchId(batchId);
        return buildResponse(batch, updated, "Batch updated successfully");
    }

    private void updateSingleNomination(Long batchId,
                                         UpdateCoursePanelBatchRequest.NominationUpdate update,
                                         List<String> errors) {
        CoursePanelNomination nomination = null;

        // Prefer lookup by nomination id
        if (update.getId() != null) {
            nomination = nominationRepository.findById(update.getId())
                    .orElse(null);
        }

        // Fallback: find by batchId + personnelId
        if (nomination == null && update.getPersonnelId() != null) {
            nomination = nominationRepository
                    .findByBatchIdAndPersonnelId(batchId, update.getPersonnelId());
        }

        if (nomination == null) {
            errors.add("Nomination not found: id=" + update.getId()
                    + ", personnelId=" + update.getPersonnelId());
            return;
        }

        // Apply only non-null fields (partial update)
        if (update.getAttendanceStatus() != null) {
            validateAttendanceStatus(update.getAttendanceStatus());
            nomination.setAttendanceStatus(update.getAttendanceStatus());
        }
        if (update.getSerialNumber() != null) {
            nomination.setSerialNumber(update.getSerialNumber());
        }
        if (update.getStatus() != null) {
            nomination.setStatus(update.getStatus());
        }
        nomination.setUpdatedAt(LocalDateTime.now());

        nominationRepository.save(nomination);
    }

    /**
     * Fetch a batch and all its nominations (read-only).
     */
    @Transactional(readOnly = true)
    public CoursePanelBatchResponse getBatch(Long batchId) {
        CoursePanelBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Batch not found with id: " + batchId));
        List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batchId);
        return buildResponse(batch, nominations, "OK");
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private void validateAttendanceStatus(String status) {
        if (!status.equalsIgnoreCase("Detailed") && !status.equalsIgnoreCase("Reserve")) {
            throw new IllegalArgumentException("Invalid attendanceStatus: " + status
                    + ". Allowed values: Detailed, Reserve");
        }
    }

    private CoursePanelBatchResponse buildResponse(CoursePanelBatch batch,
                                                    List<CoursePanelNomination> nominations,
                                                    String message) {
        List<CoursePanelBatchResponse.NominationSummary> summaries = nominations.stream()
                .map(n -> CoursePanelBatchResponse.NominationSummary.builder()
                        .nominationId(n.getId())
                        .personnelId(n.getPersonnelId())
                        .attendanceStatus(n.getAttendanceStatus())
                        .serialNumber(n.getSerialNumber())
                        .status(n.getStatus())
                        .build())
                .collect(Collectors.toList());

        return CoursePanelBatchResponse.builder()
                .batchId(batch.getId())
                .scheduleId(batch.getScheduleId())
                .status(batch.getStatus())
                .totalNominations(summaries.size())
                .message(message)
                .nominations(summaries)
                .build();
    }

    public PanelBatchListResponse getPanelBatchesByMovementAndStatus(
            Long movementId,
            String status,
            int page,
            int size) {

        // Validate inputs
        if (movementId == null || movementId < 1) {
            throw new IllegalArgumentException("Invalid movement ID");
        }

        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status. Must be PENDING_APPROVAL, APPROVED, or REJECTED");
        }

        // Create pageable
        Pageable pageable = PageRequest.of(page, size);

        // Fetch batches
        Page<CoursePanelBatch> batchPage = batchRepository.findByMovementIdLessThanEqualAndStatus(
                movementId, status, pageable);

        // Convert to DTOs with schedule and course details
        List<PanelBatchListDTO> dtos = batchPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PanelBatchListResponse.builder()
                .data(dtos)
                .totalCount(batchPage.getTotalElements())
                .currentPage(page)
                .pageSize(size)
                .totalPages(batchPage.getTotalPages())
                .build();
    }

    private PanelBatchListDTO convertToDTO(CoursePanelBatch batch) {
        PanelBatchListDTO.PanelBatchListDTOBuilder builder = PanelBatchListDTO.builder()
                .batchId(batch.getId())
                .scheduleId(batch.getScheduleId())
                .movementId(batch.getMovementId())
                .status(batch.getStatus())
                .totalNominations(batch.getTotalNominations())
                .remarks(batch.getRemarks())
                .batchStatus(batch.getBatchStatus())
                .rejectMovementId(batch.getRejectMovementId())
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt());

        // Fetch schedule details
        CourseSchedule schedule = scheduleRepository.findById(batch.getScheduleId()).orElse(null);
        if (schedule != null) {
            builder.courseId(schedule.getCourse().getSrno())
                    .year(schedule.getYear())
                    .batchNumber(schedule.getBatchNumber())
                    .startDate(schedule.getStartDate())
                    .endDate(schedule.getEndDate())
                    .venue(schedule.getVenue())
                    .courseStrength(schedule.getCourseStrength())
                    .panelSize(schedule.getPanelSize());

            // Fetch course details
            courseMasterRepository.findById(schedule.getCourse().getSrno()).ifPresent(course -> builder.courseName(course.getCourseName()));
        }

        return builder.build();
    }

    private boolean isValidStatus(String status) {
        return (
                "PENDING_APPROVAL".equals(status) ||
                        "APPROVED".equals(status) ||
                        "REJECTED".equals(status)
        );
    }

    public long getCountByMovementAndStatus(Long movementId, String status) {
        return batchRepository.countByMovementIdLessThanEqualAndStatus(movementId, status);
    }

    public List<PersonnelDataDTO> getPersonnelByBatchId(Long batchId) {
        // First check if batch exists and is active
        CoursePanelBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (!Boolean.TRUE.equals(batch.getBatchStatus())) {
            throw new IllegalStateException("Cannot view inactive batch");
        }

        // Get nominations for this batch
        List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batchId);

        // Get personnel IDs
        List<Long> personnelIds = nominations.stream()
                .map(CoursePanelNomination::getPersonnelId)
                .collect(Collectors.toList());

        // Fetch personnel details
        List<Personnel> personnelList = personnelRepository.findAllById(personnelIds);

        // Convert to DTO
        return personnelList.stream()
                .map(personnel -> PersonnelDataDTO.builder()
                        .armyNo(personnel.getArmyNo())
                        .rank(personnel.getRank())
                        .fullName(personnel.getFullName())
                        .city(personnel.getCity())
                        .district(personnel.getDistrict())
                        .mobileNumber(personnel.getMobileNumber())
                        .emailAddress(personnel.getEmailAddress())
                        .build())
                .collect(Collectors.toList());
    }
}