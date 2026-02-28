package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficerListRequestDto {
    private String formationType;
    private String unitName;
}