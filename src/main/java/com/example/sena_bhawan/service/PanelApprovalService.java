package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.OfficerNominationDTO;
import com.example.sena_bhawan.dto.PaginationInfo;
import com.example.sena_bhawan.dto.PanelApprovalDTO;
import com.example.sena_bhawan.dto.RoleWiseApprovalResponse;
import com.example.sena_bhawan.entity.CoursePanelBatch;
import com.example.sena_bhawan.entity.CoursePanelNomination;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.repository.CoursePanelBatchRepository;
import com.example.sena_bhawan.repository.CoursePanelRepository;
import com.example.sena_bhawan.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PanelApprovalService {
    
    private final CoursePanelBatchRepository batchRepository;
    private final CoursePanelRepository nominationRepository;
    private final PersonnelRepository personnelService; // Service to fetch officer details
    private final RankMasterService rankService; // Service to fetch rank details
    
    public RoleWiseApprovalResponse getRoleWiseApprovalPanels(
            Long movementId, 
            String role, 
            int page, 
            int size) {
        
        // Determine which statuses to fetch based on role and movementId
        List<String> statuses = getStatusesForRole(role, movementId);
        
        // Fetch batches with pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CoursePanelBatch> batchPage = batchRepository.findByMovementIdAndStatusIn(
                movementId, statuses, pageable);
        
        // Convert to DTOs with officer details
        List<PanelApprovalDTO> pendingPanels = new ArrayList<>();
        List<PanelApprovalDTO> approvedPanels = new ArrayList<>();
        List<PanelApprovalDTO> rejectedPanels = new ArrayList<>();
        
        for (CoursePanelBatch batch : batchPage.getContent()) {
            PanelApprovalDTO dto = convertToDTO(batch);
            
            // Add officers to the panel
            List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batch.getId());
            dto.setOfficers(convertNominationsToOfficers(nominations));
            
            // Categorize based on status
            switch (batch.getStatus()) {
                case "PENDING_APPROVAL":
                    pendingPanels.add(dto);
                    break;
                case "APPROVED":
                    approvedPanels.add(dto);
                    break;
                case "REJECTED":
                    rejectedPanels.add(dto);
                    break;
            }
        }
        
        // Build pagination info
        PaginationInfo paginationInfo = PaginationInfo.builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(batchPage.getTotalElements())
                .totalPages(batchPage.getTotalPages())
                .build();
        
        return RoleWiseApprovalResponse.builder()
                .pendingPanels(pendingPanels)
                .approvedPanels(approvedPanels)
                .rejectedPanels(rejectedPanels)
                .pagination(paginationInfo)
                .build();
    }
    
    private List<String> getStatusesForRole(String role, Long movementId) {
        // Role-based logic for which statuses to show
        switch (role.toUpperCase()) {
            case "G1":
                // G1 sees pending panels for movement 1
                if (movementId == 1) {
                    return Arrays.asList("PENDING_APPROVAL");
                }
                break;
            case "COLONEL":
                // Colonel sees pending panels for movement 2
                if (movementId == 2) {
                    return Arrays.asList("PENDING_APPROVAL");
                }
                break;
            case "GENERAL":
                // General sees all approved and rejected
                return Arrays.asList("APPROVED", "REJECTED");
        }
        return Collections.emptyList();
    }
    
    private PanelApprovalDTO convertToDTO(CoursePanelBatch batch) {
        return PanelApprovalDTO.builder()
                .batchId(batch.getId())
                .scheduleId(batch.getScheduleId())
                .movementId(batch.getMovementId())
                .status(batch.getStatus())
                .batchStatus(batch.getBatchStatus())
                .rejectMovementId(batch.getRejectMovementId())
                .totalNominations(batch.getTotalNominations())
                .remarks(batch.getRemarks())
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt())
                .build();
    }
    
    private List<OfficerNominationDTO> convertNominationsToOfficers(List<CoursePanelNomination> nominations) {
        return nominations.stream()
                .map(nomination -> {
                    // Fetch officer details from personnel service
                    Personnel personnel = personnelService.findById(nomination.getPersonnelId()).orElseThrow();
                    
                    return OfficerNominationDTO.builder()
                            .personnelId(nomination.getPersonnelId())
//                            .name(personnel.getName())
                            .armyNo(personnel.getArmyNo())
                            .rank(personnel.getRank())
//                            .unit(personnel.getUnit())
//                            .command(personnel.getCommand())
                            .dateOfCommission(formatDate(personnel.getDateOfCommission()))
                            .dateOfSeniority(formatDate(personnel.getDateOfSeniority()))
                            .dateOfBirth(formatDate(personnel.getDateOfBirth()))
                            .religion(personnel.getReligion())
                            .maritalStatus(personnel.getMaritalStatus())
//                            .medicalCategory(personnel.getMedicalCategory())
//                            .mobile(personnel.getMobile())
//                            .email(personnel.getEmail())
                            .city(personnel.getCity())
                            .state(personnel.getState())
                            .serialNumber(nomination.getSerialNumber())
                            .attendanceStatus(nomination.getAttendanceStatus())
                            .grade(nomination.getGrade())
                            .instructorAward(nomination.getInstructorAward())
                            .gradeRemarks(nomination.getGradeRemarks())
                            .gradeStatus(nomination.getGradeStatus())
                            .status(nomination.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    
    public PanelApprovalDTO getPanelDetails(Long batchId) {
        CoursePanelBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Panel not found with ID: " + batchId));

        PanelApprovalDTO dto = convertToDTO(batch);
        List<CoursePanelNomination> nominations = nominationRepository.findByBatchId(batchId);
        dto.setOfficers(convertNominationsToOfficers(nominations));

        return dto;
    }
}