package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseUnitNameMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseUnitNameMappingRepository extends JpaRepository<CourseUnitNameMapping, Long> {
    
    @Query("SELECT cum.unitName FROM CourseUnitNameMapping cum WHERE cum.course.srno = :courseId")
    List<String> findUnitNamesByCourseId(@Param("courseId") Integer courseId);
    
    @Modifying
    @Query("DELETE FROM CourseUnitNameMapping cum WHERE cum.course.srno = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);
}