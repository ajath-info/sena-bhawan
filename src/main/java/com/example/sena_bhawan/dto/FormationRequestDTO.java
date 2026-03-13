package com.example.sena_bhawan.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FormationRequestDTO {

    // What user selected (COMMAND / CORPS / DIVISION / BRIGADE / UNIT)
    private String formationType;

    // Parent IDs (nullable based on level)
    private Long hqId;
    private Long commandId;
    private Long corpsId;
    private Long divisionId;
    private Long brigadeId;

    // Common fields
    private String name;        // commandName / corpsName / etc.
    private String location;
    private String susNo;
    private String pinNo;
    private Long code;          // commandCode / corpsCode / etc.
    private String areaType;
    private String unitType; // only in case of unit

    private LocalDateTime createdAt;
}