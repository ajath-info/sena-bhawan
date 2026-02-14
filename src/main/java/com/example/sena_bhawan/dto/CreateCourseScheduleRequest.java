package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCourseScheduleRequest {

    private Integer courseId;     // maps to CourseMaster.srno
    private Long scheduleId;      // not required for create, but kept if needed
    private String courseName;    // optional / not used in create

    private String year;
    private String batchNumber;

    private String startDate;     // date as string from frontend (e.g., "2024-01-10")
    private String endDate;

    private String courseStrength;
    private String venue;
    private String remarks;
}