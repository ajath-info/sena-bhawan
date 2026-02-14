package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "personnelinformation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Army No (personnel table id)
    @Column(name = "personnel_id", nullable = false)
    private Long personnelId;

    @Column(name = "rank", nullable = false)
    private String rank;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "case_type")
    private String caseType;

    @Column(name = "case_id")
    private String caseId;

    @Column(name = "date_of_filing")
    private LocalDate dateOfFiling;

    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "final_outcome")
    private String finalOutcome;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}



