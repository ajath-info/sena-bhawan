package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OfficerTableDTO {

    private String armyNo;
    private String rank;
    private String name;
    private String gender;

    private LocalDate dob;
    private LocalDate commissionDate;
    private LocalDate seniorityDate;

    private int coursesDone;
    private int trainingYr;
    private int coursesInUnit;
}
