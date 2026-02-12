package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CourseStep1DTO {

    private Integer courseId;
    private Long scheduleId;
    private String batchNumber;
    private String courseName;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venue;
}