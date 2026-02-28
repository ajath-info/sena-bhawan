package com.example.sena_bhawan.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class OfficerListResponseDto {
    private String formationType;
    private String unitName;
    private OfficerSummaryResponseDto summary;
    private List<OfficerDetailResponseDto> officers;
}