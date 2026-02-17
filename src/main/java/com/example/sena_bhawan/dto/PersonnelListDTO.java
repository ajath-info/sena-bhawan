// PersonnelListDTO.java
package com.example.sena_bhawan.dto;

import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.entity.PersonnelQualifications;
import lombok.Data;

import java.time.format.DateTimeFormatter;

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
    private String command;
    private String corps;
    private String division;
    private String establishmentType;
    private String medicalCategory;
    private String tosStart;
    private String tosEnd;
    private String courseName;
    private String civilQual;
    private String sports;
    private String placeOfBirth;
    private String areaType;
    private String postingDueMonths;
    
    // Transform from entity
    public static PersonnelListDTO fromPersonnel(Personnel personnel) {
        PersonnelListDTO dto = new PersonnelListDTO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        dto.setId(personnel.getId());
        dto.setArmyNo(personnel.getArmyNo());
        dto.setRank(personnel.getRank());
        dto.setFullName(personnel.getFullName());
        dto.setDateOfBirth(personnel.getDateOfBirth() != null ? personnel.getDateOfBirth().format(formatter) : "—");
        dto.setDateOfCommission(personnel.getDateOfCommission() != null ? personnel.getDateOfCommission().format(formatter) : "—");
        dto.setDateOfSeniority(personnel.getDateOfSeniority() != null ? personnel.getDateOfSeniority().format(formatter) : "—");
        dto.setPlaceOfBirth(personnel.getPlaceOfBirth());
        dto.setMedicalCategory(personnel.getMedicalCategory() != null ? personnel.getMedicalCategory() : "—");
        
        // Handle nested data
        if (personnel.getQualifications() != null && !personnel.getQualifications().isEmpty()) {
            PersonnelQualifications qual = personnel.getQualifications().get(0);
            dto.setCivilQual(qual.getQualification() != null ? qual.getQualification() : "—");
            // If you have courseName in qualifications, otherwise use qualification
            dto.setCourseName(qual.getQualification() != null ? qual.getQualification() : "—");
        } else {
            dto.setCivilQual("—");
            dto.setCourseName("—");
        }
        
        // Handle sports from additionalQualifications
        if (personnel.getAdditionalQualifications() != null && !personnel.getAdditionalQualifications().isEmpty()) {
            // Assuming sports field exists in additionalQualifications
            dto.setSports("—"); // Update this based on your actual sports field
        } else {
            dto.setSports("—");
        }
        
        // Set default values for fields that don't exist in your entity
        dto.setUnit("—");
        dto.setCommand("—");
        dto.setCorps("—");
        dto.setDivision("—");
        dto.setEstablishmentType("—");
        dto.setTosStart("—");
        dto.setTosEnd("—");
        dto.setAreaType("—");
        dto.setPostingDueMonths("—");
        
        return dto;
    }
}