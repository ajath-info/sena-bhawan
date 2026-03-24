package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class ParticipantGradeUpdateDto {
    private Long personnelId;
    private String grade;
    private Boolean instructorAward;
    private String remarks;
}
