package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class CourseScheduleDto {
    private Long scheduleId;
    private Long courseId;
    private String courseName;
    private String year;
    private String batchNumber;
    private String startDate;
    private String endDate;
    private String courseStrength;
    private String venue;
    private String remarks;
    private String panelSize;
}
