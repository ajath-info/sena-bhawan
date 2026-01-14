package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class CourseDetailsRequestDTO {

    private Long personnelId;
    private Integer courseId;
    private String courseName;

    private String courseSerialNo;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String grading;
    private String remarks;

    private String letterNo;
    private LocalDate letterDate;
    private String duration;

    private String gradeCardPath;
    private String supportingDocumentPath;
}
