package com.example.sena_bhawan.dto;

import java.time.LocalDate;

public class DecorationRequest {
    public Long id; // null for new
    public String decorationCategory;
    public String decorationName;
    public LocalDate awardDate;
    public String citation;
}

