package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PostingRequestDTO {

    private Long personnelId;

    // -------- Under Posting --------
    private LocalDate movementDate;
    private String postedTo;
    private String appointment;
    private LocalDate postingOrderIssueDate;
    private String typeOfPosting;

    // -------- Posting In --------
    private LocalDate tosUpdatedDate;
    private String rank;
}
