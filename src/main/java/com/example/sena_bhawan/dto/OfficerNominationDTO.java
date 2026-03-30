package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficerNominationDTO {
    private Long personnelId;
    private String name;
    private String armyNo;
    private String rank;
    private String unit;
    private String command;
    private String dateOfCommission;
    private String dateOfSeniority;
    private String dateOfBirth;
    private String religion;
    private String maritalStatus;
    private String medicalCategory;
    private String mobile;
    private String email;
    private String city;
    private String state;
    private Long serialNumber;
    private String attendanceStatus;
    private String grade;
    private Boolean instructorAward;
    private String gradeRemarks;
    private String gradeStatus;
    private String status;
}
