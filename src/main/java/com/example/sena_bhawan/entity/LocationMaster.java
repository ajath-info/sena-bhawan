package com.example.sena_bhawan.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "location_master")
@Getter
@Setter
public class LocationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "srno")
    private Integer srno;

    @Column(name = "locationname")
    private String locationName;

    @Column(name = "state")
    private String state;
}
