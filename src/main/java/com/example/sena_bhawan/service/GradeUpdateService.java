package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.GradeUpdateRequestDto;
import com.example.sena_bhawan.dto.GradeUpdateResponseDto;

public interface GradeUpdateService {

    GradeUpdateResponseDto getGradeUpdateData(Long scheduleId);

    void saveGrades(GradeUpdateRequestDto request);

    byte[] exportToPdf(Long scheduleId);

    byte[] exportToExcel(Long scheduleId);
}
