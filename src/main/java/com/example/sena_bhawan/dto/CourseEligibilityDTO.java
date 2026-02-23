package com.example.sena_bhawan.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CourseEligibilityDTO {
    private Integer courseId;
    private LocalDate commissionDateFrom;
    private LocalDate commissionDateTo;
    private LocalDate seniorityDateFrom;
    private LocalDate seniorityDateTo;
    private LocalDate dobFrom;
    private LocalDate dobTo;
    private List<Long> rankIds;
    private List<Long> unitIds;
    private List<Long> postingTypeIds;
    private List<Long> minCourseGrading;     // Dropdown IDs for COURSE_GRADE
    private List<Long> educationalQualifications; // Dropdown IDs for CIVIL_QUALIFICATION
    private List<Long> medicalCategories;     // Dropdown IDs for MEDICAL
    private List<Long> establishmentTypes;    // Dropdown IDs for ESTABLISHMENT
    private List<String> remarks;
    private String additionalRemarks;
}