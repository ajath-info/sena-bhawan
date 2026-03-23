package com.example.sena_bhawan.dto;

import lombok.Data;
import java.util.List;

@Data
public class PanelReportDto {
    private CourseInfoDto courseInfo;
    private List<ParticipantGradeDto> participants;
}
