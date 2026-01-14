package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CourseCountDto {

    @JsonProperty("totalschedule")
    private long totalschedule;

    @JsonProperty("currentbatch")
    private long currentbatch;

    @JsonProperty("upcoming")
    private long upcoming;
}