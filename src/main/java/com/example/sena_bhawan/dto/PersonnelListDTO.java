// PersonnelListDTO.java
package com.example.sena_bhawan.dto;

import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.entity.PersonnelQualifications;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
public class PersonnelListDTO {
    private Long id;
    private String armyNo;
    private String rank;
    private String fullName;
    private String dateOfBirth;
    private String dateOfCommission;
    private String dateOfSeniority;
    private String unit;
    private String areaType;
    private String division;
    private String establishmentType;
    private String command;
    private String corps;
    private String medicalCode;
    private String course;
    private String civilQual;
    private String sports;

    // Additional fields with default "-"
    private String religion;
    private String maritalStatus;
    private String mobileNumber;
    private String emailAddress;
    private String city;
    private String state;
    private String placeOfBirth;

    // Course Panel Status (will be computed)
    private String panelStatus;
    private Integer totalCoursesDone;
    private Integer coursesTrainingYr;
    private Integer coursesInUnit;
    private String postingDueMonths;
    private String tosDate;

    // New course statistics fields
    private Integer totalCoursesOverall;
    private Integer totalCoursesCurrentYear;
    private Integer totalCoursesCurrentUnit;
}