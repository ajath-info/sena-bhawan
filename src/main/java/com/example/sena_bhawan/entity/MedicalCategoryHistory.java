package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_category_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalCategoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "personnel_id")
    private Long personnelId;

    private String category;   // S, H, A, P, E
    private Integer oldValue;
    private Integer newValue;

    private LocalDate changeDate;
    private String diagnosis;
    private LocalDate nextReviewDate;
    private String categoryType;   // TEMPORARY / PERMANENT
    private String restriction;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters setters
}

