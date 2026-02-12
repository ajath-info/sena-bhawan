package com.example.sena_bhawan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "unit_master")
public class UnitMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_name", nullable = false)
    private String unitName;

    @Column(name = "location", nullable = false)
    private String location;

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
