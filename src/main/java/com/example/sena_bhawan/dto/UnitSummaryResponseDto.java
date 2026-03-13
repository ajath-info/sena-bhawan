package com.example.sena_bhawan.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class UnitSummaryResponseDto {
    private String formationType;
    private String unitName;
    private UnitStatisticsDto statistics;
}