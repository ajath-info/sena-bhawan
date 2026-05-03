package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfSeniority;
    private String status;
}

