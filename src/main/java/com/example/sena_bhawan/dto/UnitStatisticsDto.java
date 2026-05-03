package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UnitStatisticsDto {
    private int totalOfficers;              // Total Officers in unit
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate earliestSeniority;    // Earliest Seniority
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate latestSeniority;      // Latest Seniority
    private int totalCoursesDone;            // Total Courses Done by all officers in this unit
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate earliestCommission;    // Earliest Commission
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate latestCommission;      // Latest Commission
    private Integer coursesTrainingYr;       // Courses in Training Year (current year)
    private Integer coursesInUnit;           // Total Courses available in this unit
}