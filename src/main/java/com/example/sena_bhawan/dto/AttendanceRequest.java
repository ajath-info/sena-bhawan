package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class AttendanceRequest {
    private Integer courseId;
    private Long scheduleId;
    private Long personnelId;
    private String status;
}

