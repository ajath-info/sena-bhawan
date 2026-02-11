package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CoursePanelView {

    private String fullName;
    private String armyNo;
    private String rank;
    private String unitName;
    private String command;
    private LocalDate dateOfSeniority;
}
