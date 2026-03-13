package com.example.sena_bhawan.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UnitStatisticsDto {
    private int totalOfficers;              // Total Officers in unit
    private LocalDate earliestSeniority;    // Earliest Seniority
    private LocalDate latestSeniority;      // Latest Seniority
    private int totalCoursesDone;            // Total Courses Done by all officers in this unit
    private LocalDate earliestCommission;    // Earliest Commission
    private LocalDate latestCommission;      // Latest Commission
    private Integer coursesTrainingYr;       // Courses in Training Year (current year)
    private Integer coursesInUnit;           // Total Courses available in this unit
}