package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchActionRequest {
    private Long userId;
    private String remarks;
    private Long rejectMovementId; // Only for reject action
}

