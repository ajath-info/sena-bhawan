package com.example.sena_bhawan.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OfficerSummaryResponseDto {
    private Integer totalOfficers;
    private LocalDate earliestSeniority;
    private LocalDate latestSeniority;
    private Integer totalCoursesDone;
    private LocalDate earliestCommission;
    private LocalDate latestCommission;
    private Integer coursesTrainingYr;
    private Integer coursesInUnit;
}