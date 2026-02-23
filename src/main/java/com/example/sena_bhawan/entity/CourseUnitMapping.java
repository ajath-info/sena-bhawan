package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_unit_mapping",
       uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "unit_id"}))
@Data
public class CourseUnitMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseMaster course;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitMaster unit;

    @CreationTimestamp
    private LocalDateTime createdAt;
}