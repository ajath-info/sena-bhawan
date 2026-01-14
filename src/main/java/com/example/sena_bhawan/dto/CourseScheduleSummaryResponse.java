package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CourseScheduleSummaryResponse {

    @JsonProperty("courseDetails")
    private CourseDetailsDto courseDetails;

    @JsonProperty("courseCount")
    private CourseCountDto courseCount;

    @JsonProperty("courseSchedule")
    private List<CourseScheduleItemDto> courseSchedule;
}