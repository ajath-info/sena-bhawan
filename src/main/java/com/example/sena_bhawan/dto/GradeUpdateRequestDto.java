package com.example.sena_bhawan.dto;

import lombok.Data;
import java.util.List;

@Data
public class GradeUpdateRequestDto {
    private Long scheduleId;
    private List<ParticipantGradeUpdateDto> gradeUpdates;
}