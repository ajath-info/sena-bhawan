package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class AttendanceStatusRequest {
    private String attendanceStatus;
    private Integer page = 0;
    private Integer size = 10;
}