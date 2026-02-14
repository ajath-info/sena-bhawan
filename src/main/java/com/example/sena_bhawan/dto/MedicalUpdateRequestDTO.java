package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicalUpdateRequestDTO {

    private Long personnelId;

    private String category;      // S / H / A / P / E
    private Integer newValue;     // 2,3,4,5

    private LocalDate changeDate;
    private String diagnosis;
    private LocalDate nextReviewDate;
    private String categoryType;
    private String restriction;

    // getters setters
}

