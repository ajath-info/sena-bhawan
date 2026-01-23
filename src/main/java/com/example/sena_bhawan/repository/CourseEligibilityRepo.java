package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseEligibilityMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseEligibilityRepo extends JpaRepository<CourseEligibilityMaster, Long> {

    // For single eligibility per course
    Optional<CourseEligibilityMaster> findByCourse_Srno(Integer courseId);
}
