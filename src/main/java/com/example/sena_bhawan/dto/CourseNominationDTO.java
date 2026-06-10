package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseNominationDTO {
    // Course Details
    private String courseName;
    private Integer courseId;
    private String courseLocation;
    private Integer courseDuration;
    
    // Schedule Details
    private Long scheduleId;
    private String batchNumber;
    private String year;
    private String startDate;
    private String endDate;
    private String venue;
    private String courseStrength;
    private String scheduleRemarks;
    
    // Nomination Details
    private Long nominationId;
    private String nominationStatus;
    private String attendanceStatus;
    private String grade;
    private String gradeStatus;
    private Boolean instructorAward;
    private Integer serialNumber;
    private String nominationCreatedAt;
    private String nominationUpdatedAt;
    
    // Personnel Details
    private Long personnelId;
    private String armyNo;
    private String rank;
    private String fullName;
    private String firstName;
    private String lastName;
    private String gender;
    private String commissionType;
    private String dateOfBirth;
    private String dateOfCommission;
    private String dateOfSeniority;
    private String medicalCode;
    private String religion;
    private String maritalStatus;
    private String mobileNumber;
    private String emailAddress;
    private String city;
    private String state;
    private String district;
    
    // Posting Details
    private String currentUnit;
    private String areaType;
    private String division;
    private String command;
}