package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_dropdown_mapping",
       uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "dropdown_id"}))
@Data
public class CourseDropdownMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseMaster course;

    @ManyToOne
    @JoinColumn(name = "dropdown_id", nullable = false)
    private DropdownMaster dropdown;

    @CreationTimestamp
    private LocalDateTime createdAt;
}