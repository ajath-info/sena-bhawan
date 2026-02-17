package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long totalPersonnel;
    private Long activePostings;
    private Long coursesOngoing;
    private Long pendingTransfers;  // For now leave it as 0 or null
}