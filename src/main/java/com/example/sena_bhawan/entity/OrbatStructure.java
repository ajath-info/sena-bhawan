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

    @Column(name = "command_name")
    private String commandName;
    @Column(name = "corps_name")
    private String corpsName;
    @Column(name = "division_name")
    private String divisionName;
    @Column(name = "brigade_name")
    private String brigadeName;
    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "hq_code")
    private String hqCode;
    @Column(name = "command_code")
    private String commandCode;
    @Column(name = "corps_code")
    private String corpsCode;
    @Column(name = "division_code")
    private String divisionCode;
    @Column(name = "brigade_code")
    private String brigadeCode;
    @Column(name = "unit_code")
    private String unitCode;


    @Column(name = "formation_type")
    private String formationType;  // command / corps / division / brigade / battalion
    @Column(name = "name")
    private String name;           // actual formation name

    @Column(name = "hq_id")
    private Long hqId;
    @Column(name = "location")
    private String location;
    @Column(name = "sus_no")
    private String susNo;
    @Column(name = "pin")
    private String pin;
    @Column(name = "area_type")
    private String areaType;
    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "formation_code", unique = true)
    private String formationCode;


    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    private void setDefaults() {

        if (hqCode == null) hqCode = "00";
        if (commandCode == null) commandCode = "00";
        if (corpsCode == null) corpsCode = "00";
        if (divisionCode == null) divisionCode = "00";
        if (brigadeCode == null) brigadeCode = "000";
        if (unitCode == null) unitCode = "000";

        formationCode =
                hqCode +
                        commandCode +
                        corpsCode +
                        divisionCode +
                        brigadeCode +
                        unitCode;
    }
}
