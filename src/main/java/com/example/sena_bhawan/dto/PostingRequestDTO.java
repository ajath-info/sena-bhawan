package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PostingRequestDTO {

    private String armyNo;
    private Long personnelId;
    private Long postingId;

    // -------- Under Posting --------
    private LocalDate movementDate;
    private String postedTo;
    private String appointment;
    private LocalDate postingOrderIssueDate;
    private String typeOfPosting;

    // -------- Posting In --------
    private LocalDate tosUpdatedDate;
    private String rank;

    // Internal fields (set by service)
    private Long orbatId;
    private String status;
}
