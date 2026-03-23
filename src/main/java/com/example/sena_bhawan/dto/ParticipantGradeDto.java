package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class ParticipantGradeDto {
    private Integer srNo;
    private Long personnelId;
    private String officerName;
    private String armyNo;
    private String unit;
    private String panelPosition; // attendance_status from nomination table
    private String grade;
    private Boolean instructorAward;
    private String remarks;
    private String gradeStatus; // "Graded" or "Pending"
}
