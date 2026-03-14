package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "personnel_medical_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelMedicalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medical_category")  // S, H, A, P, E
    private String medicalCategory;

    @Column(name = "medical_value")      // 1,2,3,4,5 etc
    private String medicalValue;

    @Column(name = "type")               // TEMPORARY or PERMANENT
    private String type;

    @Column(name = "period")             // duration
    private String period;

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    @JsonBackReference
    private Personnel personnel;
}