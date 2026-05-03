package com.example.sena_bhawan.dto.ParamountDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostingHistoryDto {
    private Long postingId;
    private String unitName;
    private String command;
    private String typeOfUnit;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate takenOnStrength;
    private String appointment;
    private String duration; // total time served
}
