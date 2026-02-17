package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_remarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRemarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    @Column(name = "before_detailment_status")
    private String beforeDetailmentStatus;

    @Column(name = "before_detailment_reason")
    private String beforeDetailmentReason;

    @Column(name = "after_detailment_status")
    private String afterDetailmentStatus;

    @Column(name = "after_detailment_reason")
    private String afterDetailmentReason;

    @Column(name = "general_remarks")
    private String generalRemarks;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
