package com.example.sena_bhawan.dto;

import java.time.LocalDate;

public class PersonnelFilterRequest {

    // BASIC
    public String rank;
    public String medicalCategory;
    public String placeOfBirth;

    // DATES
    public LocalDate dobGreaterThan;

    public LocalDate docFrom;
    public LocalDate docTo;

    public LocalDate dosFrom;
    public LocalDate dosTo;

    public LocalDate tosFrom;
    public LocalDate tosTo;

    // ORGANISATION
    public String command;
    public String corps;
    public String division;
    public String establishmentType;
    public String areaType;

    // COURSE
    public LocalDate courseFrom;
    public LocalDate courseTo;
    public String courseName;

    // QUALIFICATION / SPORTS
    public String civilQualification;
    public String sports;

    // POSTING
    public Integer postingDueMonths;
}
