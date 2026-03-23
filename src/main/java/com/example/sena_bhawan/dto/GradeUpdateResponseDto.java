package com.example.sena_bhawan.dto;

import lombok.Data;
import java.util.List;

@Data
public class GradeUpdateResponseDto {
    private CourseInfoDto courseInfo;
    private List<ParticipantGradeDto> participants;
}