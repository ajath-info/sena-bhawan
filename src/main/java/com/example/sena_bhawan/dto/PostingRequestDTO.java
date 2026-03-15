package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class PostingRequestDTO {

    private String armyNo;           // From UI
    private Long personnelId;         // Set by service after lookup
    private Long postingId;           // For updates (if exists)

    // -------- Under Posting --------
    private LocalDate movementDate;
    private String postedTo;
    private String appointment;
    private LocalDate postingOrderIssueDate;
    private String typeOfPosting;     // ✅ This is "Posting Type" from dropdown

    // -------- Posting In --------
    private LocalDate tosUpdatedDate;
    private String rank;

    // Internal fields (set by service)
    private Long orbatId;              // From unit search

}