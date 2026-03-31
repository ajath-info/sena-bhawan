package com.example.sena_bhawan.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CourseEligibilityDTOT {
    private Integer courseId;
    private LocalDate commissionDateFrom;
    private LocalDate commissionDateTo;
    private LocalDate seniorityDateFrom;
    private LocalDate seniorityDateTo;
    private LocalDate dobFrom;
    private LocalDate dobTo;
    private List<String> rankIds;
    private List<String> unitIds;
    private List<String> postingTypeIds;
    private List<String> minCourseGrading;     // Dropdown IDs for COURSE_GRADE
    private List<String> educationalQualifications; // Dropdown IDs for CIVIL_QUALIFICATION
    private List<String> medicalCategories;     // Dropdown IDs for MEDICAL
    private List<String> establishmentTypes;    // Dropdown IDs for ESTABLISHMENT
    private List<String> remarks;
    private String additionalRemarks;
}