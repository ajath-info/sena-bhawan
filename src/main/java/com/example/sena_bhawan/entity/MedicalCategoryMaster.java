package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "medical_category_master")
@Getter @Setter
public class MedicalCategoryMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicalCode;
    private String medicalName;
    private Boolean isActive = true;
}
