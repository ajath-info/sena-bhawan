package com.example.sena_bhawan.dto.ParamountDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalQualificationDto {
    private Long additionalQualificationId;
    private String qualification;
    private String issuingAuthority;
}
