package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "formation_establishment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationEstablishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= RELATION =================
    @Column(name = "orbat_id", nullable = false)
    private Long orbatId;

    // ================= ENUM =================
    @Enumerated(EnumType.STRING)
    @Column(name = "establishment_type", nullable = false )
    private EstablishmentType establishmentType;

    @Column(name = "formation_Type")
    private String formationType;

    @Column(name = "name")
    private String name;

    // ================= AUTHORIZED =================
    @Column(name = "total_authorized_officers")
    private Integer totalAuthOfficers = 0;

    @Column(name = "auth_lt_capt")
    private Integer authLtCapt = 0;

    @Column(name = "auth_maj")
    private Integer authMaj = 0;

    @Column(name = "auth_lt_col")
    private Integer authLtCol = 0;

    @Column(name = "auth_col")
    private Integer authCol = 0;

    @Column(name = "auth_brig")
    private Integer authBrig = 0;

    @Column(name = "auth_maj_gen")
    private Integer authMajGen = 0;

    @Column(name = "auth_lt_gen")
    private Integer authLtGen = 0;

    // ================= HARD SCALE =================
    @Column(name = "total_hard_scale ")
    private Integer totalHardScale  = 0;

    @Column(name = "hard_lt_capt")
    private Integer hardLtCapt = 0;

    @Column(name = "hard_maj")
    private Integer hardMaj = 0;

    @Column(name = "hard_lt_col")
    private Integer hardLtCol = 0;

    @Column(name = "hard_col")
    private Integer hardCol = 0;

    @Column(name = "hard_brig")
    private Integer hardBrig = 0;

    @Column(name = "hard_maj_gen")
    private Integer hardMajGen = 0;

    @Column(name = "hard_lt_gen")
    private Integer hardLtGen = 0;

    // ================= TIMESTAMP =================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum EstablishmentType {
        PE,
        WE
    }
}
