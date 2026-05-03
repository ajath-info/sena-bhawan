package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime updatedAt;
    
    // Course Schedule Details
    private Integer courseId;
    private String courseName;
    private String year;
    private String batchNumber;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;
    private String venue;
    private String courseStrength;
    private Integer panelSize;
    private String remark1;
    private String remark2;
    private String remark3;
    private String remark4;
    private String remark5;
    private String remark6;

}