package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "decoration_master")
@Getter
@Setter
public class DecorationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "award_category", nullable = false)
    private String awardCategory;

    @Column(name = "award_name", nullable = false)
    private String awardName;

    @Column(nullable = false)
    private String decoration;  // suffix like PVC, MVC, VSM, AVSM
}
