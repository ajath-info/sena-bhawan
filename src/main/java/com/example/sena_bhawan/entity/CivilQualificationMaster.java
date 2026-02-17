package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "civil_qualification_master")
@Getter
@Setter
public class CivilQualificationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String qualificationCode;
    private String qualificationName;
    private Boolean isActive = true;
}
