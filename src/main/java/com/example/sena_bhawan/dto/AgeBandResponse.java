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
public class AgeBandResponse {
    private List<String> labels;  // STATIC: ['<30','31-35','36-40','41-45','46-50','50+']
    private List<Integer> data;   // DYNAMIC: [520,1400,1800,1100,620,290]
    private String chartType;
    private String title;
}