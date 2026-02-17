package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "area_type_master")
@Getter
@Setter
public class AreaTypeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String areaCode;
    private String areaName;
    private Boolean isActive = true;
}
