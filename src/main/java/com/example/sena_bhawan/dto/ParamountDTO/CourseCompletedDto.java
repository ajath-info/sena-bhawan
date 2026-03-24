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
public class CourseCompletedDto {
    private Long nominationId;
    private String courseName;
    private String grading;
    private LocalDate fromDate;
    private LocalDate toDate;
}
