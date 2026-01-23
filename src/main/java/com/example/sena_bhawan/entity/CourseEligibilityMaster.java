package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_eligibility_master")
@Getter
@Setter
public class CourseEligibilityMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "srno", unique = true)
    private CourseMaster course;

    @Column(name = "min_years")
    private Integer minYears;

    @Column(name = "max_years")
    private Integer maxYears;

    @Column(name = "min_course_grading")
    private String minCourseGrading;

    @Column(name = "educational_qualification")
    private String educationalQualification;

    @Column(name = "max_service_limit")
    private String maxServiceLimit;

    @Column(name = "medical_category")
    private String medicalCategory;

    @Column(name = "additional_remarks")
    private String additionalRemarks;

    @OneToMany(mappedBy = "eligibility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseEligibleRank> eligibleRanks = new ArrayList<>();

    @OneToMany(mappedBy = "eligibility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseEligibleUnit> eligibleUnits = new ArrayList<>();

    @OneToMany(mappedBy = "eligibility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseEligiblePostingType> postingTypes = new ArrayList<>();


    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}

