package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course_eligible_units")
@Getter
@Setter
public class CourseEligibleUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eligibility_id")
    private CourseEligibilityMaster eligibility;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private UnitMaster unit;
}

