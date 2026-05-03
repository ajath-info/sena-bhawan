package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OfficerSummaryResponseDto {
    private Integer totalOfficers;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate earliestSeniority;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate latestSeniority;
    private Integer totalCoursesDone;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate earliestCommission;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate latestCommission;
    private Integer coursesTrainingYr;
    private Integer coursesInUnit;
}