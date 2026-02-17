package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "orbat_structure")
@Data
public class OrbatStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commandName;
    private String corpsName;
    private String divisionName;
    private String brigadeName;

    private String formationType;  // command / corps / division / brigade / battalion
    private String name;           // actual formation name

    private Long hqId;
    private String location;
    private String sosNo;
    private String pin;
    private String unitName;

    private Long parentId;

    @Column(unique = true)
    private String formationCode;


    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}
