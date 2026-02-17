package com.example.sena_bhawan.dto;

import java.time.LocalDate;

public record PanelOfficerDTO(

        Long personnelId,

        // Basic
        String fullName,
        String armyNo,
        String rank,

        // Posting
        String unitName,
        String command,

        // Dates
        LocalDate dateOfCommission,
        LocalDate dateOfSeniority,
        LocalDate dateOfBirth,

        // Personal
        String religion,
        String maritalStatus,
        String medicalCategory,

        String medicalRemark,

        // Contact
        String mobileNumber,
        String emailAddress,

        // Address
        String city,
        String state,

        // Image
        String officerImage
) {}
