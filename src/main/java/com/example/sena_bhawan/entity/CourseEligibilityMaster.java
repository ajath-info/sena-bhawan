package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_eligibility_master",
        uniqueConstraints = @UniqueConstraint(columnNames = "course_id"))
@Data
public class CourseEligibilityMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseMaster course;

    // Date Filters
    @Column(name = "commission_date_from")
    private LocalDate commissionDateFrom;

    @Column(name = "commission_date_to")
    private LocalDate commissionDateTo;

    @Column(name = "seniority_date_from")
    private LocalDate seniorityDateFrom;

    @Column(name = "seniority_date_to")
    private LocalDate seniorityDateTo;

    @Column(name = "dob_from")
    private LocalDate dobFrom;

    @Column(name = "dob_to")
    private LocalDate dobTo;

    @Column(name = "additional_remarks", columnDefinition = "text")
    private String additionalRemarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}