package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitSummaryRequestDto {
    private String formationType;
    private String unitName;
}