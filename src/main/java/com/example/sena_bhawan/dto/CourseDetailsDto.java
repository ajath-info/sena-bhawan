package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CourseDetailsDto {

    @JsonProperty("courseName")
    private String courseName;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("location")
    private String location;

    @JsonProperty("srno")
    private Integer srno;
}