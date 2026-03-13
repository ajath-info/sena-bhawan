package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OfficerDetailResponseDto {
    private String armyNo;
    private String rank;
    private String fullName;
    private String gender;

    private LocalDate dateOfBirth;

    private LocalDate dateOfCommission;

    private LocalDate dateOfSeniority;

    private Integer coursesDone;
    private Integer trainingYr;
    private Integer coursesInUnit;
}