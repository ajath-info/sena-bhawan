package com.example.sena_bhawan.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
public class PersonnelFilterRequest {
    // Basic filters
    public String armyNo;
    public String placeOfBirth;
    public LocalDate dobGreaterThan;
    public LocalDate docFrom;
    public LocalDate docTo;
    public LocalDate dosFrom;
    public LocalDate dosTo;
    public String search;

    // Multi-select filters
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

    // Additional filters
    public String courseName;
    public LocalDate courseFrom;
    public LocalDate courseTo;
    public LocalDate tosFrom;
    public LocalDate tosTo;

    // Pagination
    public int page = 0;
    public int size = 10;
}