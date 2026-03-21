package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "remarks_update")
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

    @Column(name = "remark_type")
    private String remarkType;

    @Column(name = "general_remarks", columnDefinition = "TEXT")
    private String generalRemarks;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}