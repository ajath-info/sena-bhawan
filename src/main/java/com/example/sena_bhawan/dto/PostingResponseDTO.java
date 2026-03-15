package com.example.sena_bhawan.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Data
public class PostingResponseDTO {
    private Long postingId;
    private Long personnelId;
    private String armyNo;
    private String personnelName;
    private String rank;
    private String unitName;

    // UNDER POSTING
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate movementDate;
    private String postedTo;
    private String appointment;
    private String typeOfPosting;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate postingOrderIssueDate;

    // POSTING IN
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate tosUpdatedDate;
    private String rankOnPromotion;

    // Calculated
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fromDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate toDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate sosDate;
    private String duration;
    private String status;  // UNDER_POSTING or POSTED

    // ORBAT info
    private Long orbatId;
    private String formationType;
    private String formationCode;
    private String location;
}
