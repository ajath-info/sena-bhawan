package com.example.sena_bhawan.dto.ParamountDTO;

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
    private LocalDate dateOfBirth;
    private LocalDate dateOfCommission;
    private LocalDate dateOfSeniority;
    private String panCard;
    private String aadhaarNumber;

    // Service Section
    private String lastRank; // Substantive rank
}
