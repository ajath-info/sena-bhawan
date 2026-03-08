package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CourseDetailsRepository extends JpaRepository<CourseDetails, Long> {

    // 1. Fetch all courses for given personnel (entire service)
    List<CourseDetails> findByPersonnelIdIn(List<Long> personnelIds);

    List<CourseDetails> findByPersonnelId(Long personnelId);

    // 2. Get ALL distinct course IDs ever done in this unit (for Courses in This Unit)
    @Query(value = """
            SELECT DISTINCT cd.course_id 
            FROM course_details cd 
            WHERE cd.personnel_id IN (
                SELECT pd.personnel_id 
                FROM posting_details pd 
                WHERE pd.formation_type = :formationType 
                AND pd.unit_name = :unitName
            )
            """, nativeQuery = true)
    Set<Integer> findAllDistinctCourseIdsByUnit(
            @Param("formationType") String formationType,
            @Param("unitName") String unitName);

    // 3. Get unit-specific courses with posting period validation (single optimized query)
    @Query(value = """
            SELECT cd.* 
            FROM course_details cd 
            INNER JOIN posting_details pd ON cd.personnel_id = pd.personnel_id 
            WHERE pd.formation_type = :formationType 
            AND pd.unit_name = :unitName 
            AND cd.personnel_id IN :personnelIds
            AND cd.from_date BETWEEN pd.from_date AND COALESCE(pd.to_date, CURRENT_DATE)
            """, nativeQuery = true)
    List<CourseDetails> findUnitCoursesByPersonnelIds(
            @Param("personnelIds") List<Long> personnelIds,
            @Param("formationType") String formationType,
            @Param("unitName") String unitName);
}