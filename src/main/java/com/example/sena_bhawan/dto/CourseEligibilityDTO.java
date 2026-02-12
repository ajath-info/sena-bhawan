package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseEligibilityDTO {

    private Integer courseId;

    private Integer minYears;
    private Integer maxYears;

    private List<Long> rankIds;
    private List<Long> unitIds;
    private List<Long> postingTypeIds;

    private String minCourseGrading;
    private String educationalQualification;
    private String maxServiceLimit;

    private String medicalCategory;
    private String additionalRemarks;
}

