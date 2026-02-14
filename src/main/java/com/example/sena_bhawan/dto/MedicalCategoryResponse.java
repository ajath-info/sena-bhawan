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
public class MedicalCategoryResponse {
    private List<String> labels;
    private List<Integer> data;
    private String chartType;
    private String title;
}