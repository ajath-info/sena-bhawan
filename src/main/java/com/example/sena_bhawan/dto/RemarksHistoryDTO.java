package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime createdAt;
}