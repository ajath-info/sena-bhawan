package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate earliestSeniority;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate latestSeniority;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate earliestCommission;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate latestCommission;
}
