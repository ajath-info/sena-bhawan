package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "establishment_type_master")
@Getter
@Setter
public class EstablishmentTypeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String estCode;
    private String estName;
    private Boolean isActive = true;
}
