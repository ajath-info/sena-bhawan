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
public class RankStrengthResponse {
    private List<String> labels;  // STATIC: ['Lt', 'Capt', 'Maj', 'Lt Col', 'Col']
    private List<Integer> data;   // DYNAMIC: [820, 2100, 1850, 960, 420]
    private String chartType;
    private String title;
}