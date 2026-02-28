package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.OfficerListRequestDto;
import com.example.sena_bhawan.dto.OfficerListResponseDto;

public interface OfficerDetailsService {
    OfficerListResponseDto getOfficersByFormationAndUnit(OfficerListRequestDto requestDto);
}