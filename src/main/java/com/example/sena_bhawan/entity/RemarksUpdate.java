package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "remarks_update",
        uniqueConstraints = @UniqueConstraint(columnNames = "personnel_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemarksUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "personnel_id", nullable = false)
    private Long personnelId;

    @Column(name = "before_detailment", columnDefinition = "TEXT")
    private String beforeDetailment;

    @Column(name = "after_detailment", columnDefinition = "TEXT")
    private String afterDetailment;

    @Column(name = "general_remarks", columnDefinition = "TEXT")
    private String generalRemarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

