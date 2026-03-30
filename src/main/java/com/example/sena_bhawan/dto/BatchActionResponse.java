package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchActionResponse {
    private Long batchId;
    private Long scheduleId;
    private Long movementId;
    private String status;
    private String message;
    private Long rejectMovementId;
}