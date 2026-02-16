package com.example.sena_bhawan.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "unit_establishment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitEstablishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= RELATION =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitMaster unit;

    @Column(name = "establishment_type", length = 10, nullable = false)
    private String establishmentType; // PE or WE

    // ================= AUTHORIZED =================
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

    // ================= TOTALS =================
    @Column(name = "total_authorized_officers")
    private Integer totalAuthorizedOfficers = 0;

    @Column(name = "total_hard_scale")
    private Integer totalHardScale = 0;

    // ================= AUDIT =================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================= LIFECYCLE =================
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

