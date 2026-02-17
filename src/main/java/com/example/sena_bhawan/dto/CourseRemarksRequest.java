package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class CourseRemarksRequest {

    private Long personnelId;

    private String beforeDetailmentStatus;   // VOLUNTEER / NOT_TO_DETAIL
    private String beforeDetailmentReason;

    private String afterDetailmentStatus;    // TAKEN_OFF / CONTINUING
    private String afterDetailmentReason;

    private String generalRemarks;
}
