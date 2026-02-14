package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "course_schedule")
@Getter
@Setter
public class CourseSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    // MANY schedules belong to ONE course
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "srno", nullable = false)
    @JsonBackReference
    private CourseMaster course;

    @Column(name = "year")
    private String year;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "course_strength")
    private String courseStrength;

    @Column(name = "venue")
    private String venue;

    @Column(name = "remarks")
    private String remarks;

}