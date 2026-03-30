package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanelApprovalDTO {
    private Long batchId;
    private Long scheduleId;
    private Long movementId;
    private String status;
    private Boolean batchStatus;
    private Long rejectMovementId;
    private Integer totalNominations;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OfficerNominationDTO> officers;
}