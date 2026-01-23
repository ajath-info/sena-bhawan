package com.example.sena_bhawan.dto;



import java.time.LocalDate;

public record PanelOfficerDTO(
        Long personnelId,
        String fullName,
        String armyNo,
        String rank,
        String unitName,
        String command,
        LocalDate dateOfSeniority
) {}
