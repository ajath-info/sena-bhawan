package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_rank_mapping",
       uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "rank_id"}))
@Data
public class CourseRankMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseMaster course;

    @ManyToOne
    @JoinColumn(name = "rank_id", nullable = false)
    private RankMaster rank;

    @CreationTimestamp
    private LocalDateTime createdAt;
}