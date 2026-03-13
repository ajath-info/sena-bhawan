package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_unit_name_mapping")
@Data
public class CourseUnitNameMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseMaster course;

    @Column(name = "unit_name", nullable = false)
    private String unitName;

    @CreationTimestamp
    private LocalDateTime createdAt;
}