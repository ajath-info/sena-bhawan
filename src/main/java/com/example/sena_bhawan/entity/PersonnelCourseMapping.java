package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "personnel_course_mapping",
       uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "unit_id"}))
@Data
public class PersonnelCourseMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseMaster course;

    @JoinColumn(name = "medical_code", nullable = false)
    private String medicalCode;

    @CreationTimestamp
    private LocalDateTime createdAt;
}