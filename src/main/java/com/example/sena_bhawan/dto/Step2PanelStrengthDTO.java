package com.example.sena_bhawan.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Step2PanelStrengthDTO {

    private Integer courseId;
    private String courseStrength;   // STRING
    private Integer buffer;
    private Integer panelSize;
}