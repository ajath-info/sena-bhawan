package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PersonnelCourseMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PersonnelCourseRepository extends JpaRepository<PersonnelCourseMapping, Long> {


    @Modifying
    @Transactional
    @Query("DELETE FROM PersonnelCourseMapping cum WHERE cum.course.srno = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT cum.medicalCode FROM PersonnelCourseMapping cum WHERE cum.course.srno = :courseId")
    List<String> findMedicalCodesByCourseId(@Param("courseId") Integer courseId);

    boolean existsByCourseSrnoAndMedicalCode(Integer courseId, String medicalCode);
}

