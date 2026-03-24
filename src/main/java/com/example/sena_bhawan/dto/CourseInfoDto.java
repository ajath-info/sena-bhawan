package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class CourseInfoDto {
    private String courseName;
    private String courseCode;
    private String startDate;
    private String endDate;
    private String location;
    private Integer totalParticipants;
    private String completionDate;
    private String status; // "Completed"
}