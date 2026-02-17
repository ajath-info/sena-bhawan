package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetirementForecastResponse {
    private List<String> labels;  // ['2025', '2026', '2027', '2028', '2029']
    private List<Integer> data;    // [180, 240, 310, 380, 420]
    private String chartType;
    private String title;
}