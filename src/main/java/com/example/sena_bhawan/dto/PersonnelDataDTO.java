package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelDataDTO {
    private String armyNo;
    private String rank;
    private String fullName;
    private String city;
    private String district;
    private String mobileNumber;
    private String emailAddress;
}