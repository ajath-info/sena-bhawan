package com.example.sena_bhawan.dto.ParamountDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecorationDto {
    private Long decorationId;
    private String awardName;
    private LocalDate awardDate;
    private String citation;
    private String initials;
}
