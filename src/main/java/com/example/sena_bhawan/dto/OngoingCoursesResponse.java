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
public class OngoingCoursesResponse {
    private List<String> labels;  // Course names
    private List<Integer> data;    // Officer counts
    private String chartType;
    private String title;
}