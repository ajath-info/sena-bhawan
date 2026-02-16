package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemarksUpdateDTO {

    private Long personnelId;
    private String beforeDetailment;
    private String afterDetailment;
    private String generalRemarks;
}