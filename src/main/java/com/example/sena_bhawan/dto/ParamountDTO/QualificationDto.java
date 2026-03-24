package com.example.sena_bhawan.dto.ParamountDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualificationDto {
    private Long qualificationId;
    private String qualification;
    private String institution;
    private Integer year;
    private String grade;
}
