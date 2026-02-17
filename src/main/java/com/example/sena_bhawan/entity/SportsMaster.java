package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sports_master")
@Getter
@Setter
public class SportsMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sportCode;
    private String sportName;
    private Boolean isActive = true;
}
