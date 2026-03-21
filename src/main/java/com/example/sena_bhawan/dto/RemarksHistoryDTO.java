package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemarksHistoryDTO {
    private Long id;
    private Long personnelId;
    private String remarkType;
    private String generalRemarks;
    private String courseName;
    private LocalDateTime createdAt;
}