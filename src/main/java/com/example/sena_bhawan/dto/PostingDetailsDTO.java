package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class PostingDetailsDTO {

    private Long postingId;
    private Long personnelId;

    private String unitName;
    private String location;
    private String command;
    private String appointment;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fromDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate toDate;
    private String duration;

    private String remarks;
    private String documentPath;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate movementDate;
    private String postedTo;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate postingOrderIssueDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate tosUpdatedDate;
    private String rank;
}
