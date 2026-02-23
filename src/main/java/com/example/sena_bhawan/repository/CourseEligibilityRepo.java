package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseEligibilityMaster;
import com.example.sena_bhawan.entity.CourseMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CourseEligibilityRepo extends JpaRepository<CourseEligibilityMaster, Long> {

    // For single eligibility per course
    Optional<CourseEligibilityMaster> findByCourse_Srno(Integer courseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CourseEligibilityMaster cem WHERE cem.course.srno = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);

    CourseEligibilityMaster findByCourse(CourseMaster course);
}
