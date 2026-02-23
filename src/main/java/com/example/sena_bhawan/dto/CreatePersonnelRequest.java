package com.example.sena_bhawan.dto;

import java.time.LocalDate;
import java.util.List;

public class CreatePersonnelRequest {

    // PERSONNEL FIELDS
    public String commissionType;
    public String armyNo;
    public String rank;
    public String fullName;
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

    public String permanentAddress;
    public String city;
    public String district;
    public String state;
    public String pinCode;

    public String mobileNumber;
    public String alternateMobile;
    public String emailAddress;
    public String nsgEmail;

    public String medicalCategory;
    public LocalDate medicalDate;
    public String diagnosis;
    public LocalDate reviewDate;
    public String restriction;
    public String injuryCategory;
    public String irsTransfer;


    public String medicalRemark;


    // CHILD LISTS
    public List<DecorationDTO> decorations;
    public List<QualificationDTO> qualifications;
    public List<AdditionalQualificationDTO> additionalQualifications;
    public List<SportsDTO> sports;
    public List<FamilyDTO> family;



    // CHILD DTOs
    public static class DecorationDTO {

        public Long id; // NEW (optional)
        public String decorationCategory;
        public String decorationName;
        public LocalDate awardDate;
        public String citation;
    }


    public static class QualificationDTO {
        public Long id; // NEW (optional)
        public String qualification;
        public String stream;
        public String institution;
        public Integer yearOfCompletion;
        public String gradePercentage;

    }

    public static class AdditionalQualificationDTO {
        public Long id; // NEW (optional)
        public String qualification;
        public String issuingAuthority;
        public Integer year;
        public LocalDate validity;
    }

    public static class SportsDTO {
        public String sportName;
        public String level;
        public String remarks;
    }

    public static class FamilyDTO {
        public Long id; // NEW (optional)
        public String name;
        public String relationship;
        public String contactNumber;
    }

}
