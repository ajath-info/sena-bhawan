package com.example.sena_bhawan.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelDTO {
    private Long id;
    private String armyNo;
    private String rank;
    private String fullName;
}
