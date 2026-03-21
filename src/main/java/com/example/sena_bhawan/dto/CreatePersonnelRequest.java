package com.example.sena_bhawan.dto;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreatePersonnelRequest {

    // Basic Information
    private String commission;
    private String armyNo;
    private String rank;
    private String firstName;
    private String lastName;
    private String fullName;
    private String gender;
    private String caseType;
    private LocalDate dateOfCommission;
    private LocalDate dateOfSeniority;
    private LocalDate dateOfBirth;
    private String placeOfBirth;

    // Service Details
    private String nrs;
    private String nearestAirport;
    private String religion;
    private String aadhaarNumber;
    private String panCard;
    private String maritalStatus;
    private String cdaAccountNo;

    // Address
    private String permanentAddress;
    private String city;
    private String district;
    private String state;
    private String pinCode;

    // Contact
    private String mobileNumber;
    private String alternateMobile;
    private String emailAddress;
    private String nicEmail;

    // Collections
    private List<DecorationDTO> decorations;
    private List<QualificationDTO> qualifications;
    private List<AdditionalQualificationDTO> additionalQualifications;
    private List<SportsDTO> sports;
    private List<FamilyDTO> family;
    private MedicalDTO medical;

    // Nested DTOs
    @Data
    public static class DecorationDTO {
        private String decorationCategory;
        private String decorationName;
    }

    @Data
    public static class QualificationDTO {
        private String qualification;
        private String board;
        private Integer yearOfCompletion;
        private String institution;
        private String gradePercentage;
        private String part2OrderNo;
    }

    @Data
    public static class AdditionalQualificationDTO {
        private String qualification;
        private String authorityNo;
        private LocalDate date;
        private String location;
        private String part2OrderNo;
    }

    @Data
    public static class SportsDTO {
        private String sportName;
        private String sportsLevel;
        private String place;
        private String achievements;
    }

    @Data
    public static class FamilyDTO {
        private String firstName;
        private String lastName;
        private String fullName;
        private String relationship;
        private String contactNumber;
        private String part2OrderNo;
        private LocalDate date;
    }

    @Data
    public static class MedicalDTO {
        private MedicalValuesDTO medicalValues;
        private List<MedicalDetailDTO> medicalDetails;
        private String medicalCode;
    }

    @Data
    public static class MedicalValuesDTO {
        private String S;
        private String H;
        private String A;
        private String P;
        private String E;
    }

    @Data
    public static class MedicalDetailDTO {
        private String category;
        private String value;
        private String type;
        private String period;
        private String remark;
    }
}