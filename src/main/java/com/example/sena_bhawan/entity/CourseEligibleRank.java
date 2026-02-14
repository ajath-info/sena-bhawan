package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "course_eligible_ranks")
@Getter
@Setter
public class CourseEligibleRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eligibility_id")
    private CourseEligibilityMaster eligibility;

    @ManyToOne
    @JoinColumn(name = "rank_id")
    private RankMaster rank;
}

