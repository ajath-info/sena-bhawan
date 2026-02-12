package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "personnel_decorations")
@Getter
@Setter
public class PersonnelDecorations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "decoration_category")
    private String decorationCategory;

    private String decorationName;
    private LocalDate awardDate;
    private String citation;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    @JsonBackReference
    private Personnel personnel;
}
