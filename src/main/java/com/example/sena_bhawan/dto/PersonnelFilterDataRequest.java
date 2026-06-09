package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class PersonnelFilterDataRequest {
    
    // Medical Filter
    private String medicalCode;
    
    // Rank Filter (single rank only)
    private String rank;
    
    // Age Band Filter (frontend sends these exact values)
    private String ageBand; // "<30", "31-35", "36-40", "41-45", "46-50", "50+"

    // Retirement Filter
    private Integer retirementYear;
    
    // Pagination
    private Integer page = 0;
    private Integer size = 10;
}