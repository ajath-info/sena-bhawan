package com.example.sena_bhawan.dto;

import java.time.LocalDate;

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

