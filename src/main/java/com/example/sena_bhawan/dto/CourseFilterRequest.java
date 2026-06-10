package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class CourseFilterRequest {
    private String courseName;
    private Integer page = 0;
    private Integer size = 10;
}