package com.example.sena_bhawan.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CourseEligibilityDTO2 {
    private Integer courseId;
    private LocalDate commissionDateFrom;
    private LocalDate commissionDateTo;
    private LocalDate seniorityDateFrom;
    private LocalDate seniorityDateTo;
    private LocalDate dobFrom;
    private LocalDate dobTo;
    
    // Change from List<Long> to List<String> for unit names
    private List<String> unitNames;
    private List<Long> rankIds;
    private List<Long> postingTypeIds;
    private List<Long> minCourseGrading;
    private List<Long> educationalQualifications;
    private List<Long> medicalCategories;
    private List<Long> establishmentTypes;
    private List<String> remarks;
    private String additionalRemarks;
}