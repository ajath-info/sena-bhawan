package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseScheduleItemDto {

    @JsonProperty("scheduleId")
    private Long scheduleId;

    @JsonProperty("year")
    private String year;

    @JsonProperty("batchNumber")
    private String batchNumber;

    @JsonProperty("startDate")
    private LocalDate startDate;

    @JsonProperty("endDate")
    private LocalDate endDate;

    @JsonProperty("courseStrength")
    private String courseStrength;

    @JsonProperty("venue")
    private String venue;


    @JsonProperty("remarks")
    private String remarks;

    // nested course object as in your sample
    @JsonProperty("courseid")
    private Integer courseId;
}