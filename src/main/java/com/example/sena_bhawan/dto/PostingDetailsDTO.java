package com.example.sena_bhawan.dto;

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

    private LocalDate fromDate;
    private LocalDate toDate;
    private String duration;

    private String remarks;
    private String documentPath;
}
