package com.example.sena_bhawan.dto.ParamountDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityAndServiceDto {
    // Identity Section
    private String armyNo;
    private String rank;
    private String fullName;
    private String gender;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfCommission;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfSeniority;
    private String panCard;
    private String aadhaarNumber;
    private String lastRank;
    private String mobileNumber;
    private String officerImage;
    private String decorationInitials;
}
