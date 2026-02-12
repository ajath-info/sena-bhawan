package com.example.sena_bhawan.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceResponse {

    private Long personnelId;
    private String fullName;
    private String armyNo;
    private String rank;
    private String unitName;
    private String command;
    private LocalDate dateOfSeniority;
    private String status;
}

