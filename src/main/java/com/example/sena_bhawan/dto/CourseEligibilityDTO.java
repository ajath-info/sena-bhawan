package com.example.sena_bhawan.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class CourseEligibilityDTO {
    private Integer courseId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate commissionDateFrom;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate commissionDateTo;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate seniorityDateFrom;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate seniorityDateTo;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dobFrom;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dobTo;
    private List<Long> rankIds;
    private List<Long> unitIds;
    private List<Long> postingTypeIds;
    private List<Long> minCourseGrading;     // Dropdown IDs for COURSE_GRADE
    private List<Long> educationalQualifications; // Dropdown IDs for CIVIL_QUALIFICATION
    private List<String> medicalCategories;     // Dropdown IDs for MEDICAL
    private List<Long> establishmentTypes;    // Dropdown IDs for ESTABLISHMENT
    private List<String> remarks;
    private String additionalRemarks;
}