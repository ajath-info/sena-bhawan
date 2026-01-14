package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "course_details")
@Getter @Setter
public class CourseDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "personnel_id", nullable = false)
    private Long personnelId;

    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    private String courseName;
    private String institute;
    private String location;

    private String courseSerialNo;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String duration;

    private String grading;
    private String remarks;

    private String letterNo;
    private LocalDate letterDate;

    private String gradeCardPath;
    private String supportingDocumentPath;
}
