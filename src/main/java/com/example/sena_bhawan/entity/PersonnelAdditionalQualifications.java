package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "personnel_additional_qualifications")
@Setter
@Getter
public class PersonnelAdditionalQualifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String qualification;
    private String issuingAuthority;
    private Integer year;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validity;

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    @JsonBackReference
    private Personnel personnel;

    // getters/setters
}

