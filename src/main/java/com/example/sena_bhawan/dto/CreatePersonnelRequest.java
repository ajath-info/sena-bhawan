package com.example.sena_bhawan.dto;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePersonnelRequest {

    // PERSONNEL FIELDS
    @JsonProperty("commission")
    public String commissionType;

    public String armyNo;
    public String rank;

    @JsonProperty("firstName")
    public String firstName;

    @JsonProperty("lastName")
    public String lastName;

    public String fullName;

    @JsonProperty("caseType")
    public String caseType;

    public LocalDate dateOfCommission;
    public LocalDate dateOfSeniority;
    public LocalDate dateOfBirth;
    public String placeOfBirth;
    public String officerImage;

    public String nrs;
    public String religion;
    public String aadhaarNumber;
    public String panCard;
    public String maritalStatus;
    public String cdaAccountNo;
    public String gender;

    public String permanentAddress;
    public String city;
    public String district;
    public String state;
    public String pinCode;

    public String mobileNumber;
    public String alternateMobile;
    public String emailAddress;

    @JsonProperty("nicEmail")
    public String nicEmail;

    public String medicalCategory;
    public String medicalRemark;
    public String medicalCode;

    // CHILD LISTS
    public List<DecorationDTO> decorations;
    public List<QualificationDTO> qualifications;
    public List<AdditionalQualificationDTO> additionalQualifications;
    public List<SportsDTO> sports;
    public List<FamilyDTO> family;

    // NEW: Medical data
    public MedicalDTO medical;

    // CHILD DTOs
    public static class DecorationDTO {
        public Long id;
        public String decorationCategory;
        public String decorationName;
        public LocalDate awardDate;
        public String citation;
    }

    public static class QualificationDTO {
        public Long id;
        public String qualification;
        public String stream;
        public String institution;
        public Integer yearOfCompletion;
        public String gradePercentage;
    }

    public static class AdditionalQualificationDTO {
        public Long id;
        public String qualification;
        public String issuingAuthority;
        public Integer year;
        public String authorityNo;
        public String location;
        public String part2OrderNo;
        public LocalDate orderDate;
        public LocalDate validity;
    }

    public static class SportsDTO {
        public String sportName;

        @JsonProperty("sportsLevel")
        public String level;

        public String achievements;
    }

    public static class FamilyDTO {
        public Long id;
        public String name;
        public String relationship;
        public String contactNumber;
        public String part2OrderNo;
        public LocalDate orderDate;
    }

    // NEW: Medical DTOs
    public static class MedicalDTO {
        @JsonProperty("medicalValues")
        public MedicalValuesDTO medicalValues;

        public List<MedicalDetailDTO> medicalDetails;
    }

    public static class MedicalValuesDTO {
        public String S;
        public String H;
        public String A;
        public String P;
        public String E;
    }

    public static class MedicalDetailDTO {
        public String category;
        public String value;
        public String type;      // TEMPORARY or PERMANENT
        public String period;     // duration in months/years
        public String remark;
    }
}