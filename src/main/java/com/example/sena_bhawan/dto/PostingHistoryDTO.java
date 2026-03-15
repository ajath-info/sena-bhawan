package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class PostingHistoryDTO {
    private Long postingId;
    private String unitName;           // postedTo
    private String appointment;
    private String typeOfPosting;
    private String rank;                // Rank during that posting
    private String duration;             // Calculated (e.g., "3 yrs 2 m")
    private String status;               // PREVIOUS_POSTING
}