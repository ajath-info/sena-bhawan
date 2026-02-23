package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseUnitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CourseUnitMappingRepository extends JpaRepository<CourseUnitMapping, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM CourseUnitMapping cum WHERE cum.course.srno = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT cum.unit.id FROM CourseUnitMapping cum WHERE cum.course.srno = :courseId")
    List<Long> findUnitIdsByCourseId(@Param("courseId") Integer courseId);

}