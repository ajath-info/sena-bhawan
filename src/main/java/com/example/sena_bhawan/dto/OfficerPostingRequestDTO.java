package com.example.sena_bhawan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;



@Data
public class OfficerPostingRequestDTO {

    @NotNull
    private Long personnelId;   // 🔥 REQUIRED

    private String officerArmyNo;
    private String officerName;
    private LocalDate movementDate;
    private String postedToUnit;
    private String appointment;
    private LocalDate postingOrderIssueDate;
    private String postingType;
    private LocalDate tosUpdateDate;
    private String rankOnPromotion;
}

//@Data
//public class OfficerPostingRequestDTO {
//
//    private Long personnelId;
//
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate movementDate;
//
//    private String postedToUnit;
//
//    private Long appointmentId;
//
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate postingOrderIssueDate;
//
//    private Long postingTypeId;
//
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate tosUpdateDate;
//
//    private Long rankId; // Same Rank / Promotion Rank
//}
