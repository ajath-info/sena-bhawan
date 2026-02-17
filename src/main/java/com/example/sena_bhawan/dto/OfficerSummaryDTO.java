package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OfficerSummaryDTO {

    private int totalOfficers;
    private int totalCoursesDone;
    private int coursesTrainingYr;
    private int coursesInUnit;

    private LocalDate earliestSeniority;
    private LocalDate latestSeniority;
    private LocalDate earliestCommission;
    private LocalDate latestCommission;
}
