package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course_eligible_posting_types")
@Getter
@Setter
public class CourseEligiblePostingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eligibility_id")
    private CourseEligibilityMaster eligibility;

    @ManyToOne
    @JoinColumn(name = "posting_type_id")
    private PostingTypeMaster postingType;
}

