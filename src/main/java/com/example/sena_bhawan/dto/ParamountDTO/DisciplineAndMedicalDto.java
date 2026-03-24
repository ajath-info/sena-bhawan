package com.example.sena_bhawan.dto.ParamountDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisciplineAndMedicalDto {
    private String disciplineCase;
    private String restrictions;
    private String vigilanceStatus;
    private String medicalCategory;
    private String schoolInstitute;
    private String degree;
    private String course;
    private String homeStation;
}