package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.UnitSummaryRequestDto;
import com.example.sena_bhawan.dto.UnitSummaryResponseDto;

public interface UnitSummaryService {
    UnitSummaryResponseDto getUnitSummary(UnitSummaryRequestDto requestDto);
}
