package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UnitCourseRepository extends JpaRepository<CourseDetails, Long> {

    // Get all courses done by personnel in this unit during their posting period
    @Query("SELECT c FROM CourseDetails c WHERE c.personnelId IN :personnelIds AND c.fromDate BETWEEN :startDate AND :endDate")
    List<CourseDetails> findCoursesInUnitForPersonnel(
            @Param("personnelIds") List<Long> personnelIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Get courses done in current year (Training Year)
    @Query("SELECT c FROM CourseDetails c WHERE c.personnelId IN :personnelIds AND YEAR(c.fromDate) = :year")
    List<CourseDetails> findCoursesByYear(
            @Param("personnelIds") List<Long> personnelIds,
            @Param("year") int year);

    // Count total courses available in unit (from course_master)
//    @Query("SELECT COUNT(cm) FROM CourseMaster cm WHERE cm.unitName = :unitName")
//    int countCoursesInUnit(@Param("unitName") String unitName);
}