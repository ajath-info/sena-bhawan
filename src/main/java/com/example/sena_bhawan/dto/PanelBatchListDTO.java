package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanelBatchListDTO {
    private Long batchId;
    private Long scheduleId;
    private Long movementId;
    private String status;
    private Integer totalNominations;
    private String remarks;
    private Boolean batchStatus;
    private Long rejectMovementId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Course Schedule Details
    private Integer courseId;
    private String courseName;
    private String year;
    private String batchNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venue;
    private String courseStrength;
    private Integer panelSize;
}