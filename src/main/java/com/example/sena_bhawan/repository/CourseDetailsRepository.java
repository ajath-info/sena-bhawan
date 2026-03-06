package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CourseDetailsRepository extends JpaRepository<CourseDetails, Long> {
    List<CourseDetails> findByPersonnelId(Long personnelId);

    List<CourseDetails> findByPersonnelIdIn(List<Long> personnelIds);

    @Query("SELECT c FROM CourseDetails c WHERE c.personnelId = :personnelId " +
            "AND c.fromDate BETWEEN :startDate AND :endDate")
    List<CourseDetails> findCoursesInDateRange(
            @Param("personnelId") Long personnelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT c FROM CourseDetails c WHERE c.personnelId IN :personnelIds " +
            "AND YEAR(c.fromDate) = :year")
    List<CourseDetails> findByPersonnelIdsAndYear(
            @Param("personnelIds") List<Long> personnelIds,
            @Param("year") int year);
}
