package com.example.sena_bhawan.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class PersonnelFilterRequest {

    // Single values
    public String placeOfBirth;
    public LocalDate dobGreaterThan;
    public LocalDate docFrom;
    public LocalDate docTo;
    public LocalDate dosFrom;
    public LocalDate dosTo;
    public LocalDate tosFrom;
    public LocalDate tosTo;
    public LocalDate courseFrom;
    public LocalDate courseTo;
    public String courseName;
    public String search;

    // Multi-select fields (Lists)
    public List<String> rank;
    public List<String> medicalCategory;
    public List<String> command;
    public List<String> corps;
    public List<String> division;
    public List<String> establishmentType;
    public List<String> areaType;
    public List<String> civilQualification;
    public List<String> sports;
    public List<Integer> postingDueMonths;
}