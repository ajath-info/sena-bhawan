package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseDropdownMapping;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseDropdownMappingRepository extends JpaRepository<CourseDropdownMapping, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM CourseDropdownMapping cdm WHERE cdm.course.srno = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT cdm.dropdown.id FROM CourseDropdownMapping cdm " +
            "WHERE cdm.course.srno = :courseId AND cdm.dropdown.type = :type")
    List<Long> findDropdownIdsByCourseIdAndType(@Param("courseId") Integer courseId,
                                                @Param("type") String type);

    @Query("SELECT cdm FROM CourseDropdownMapping cdm " +
            "WHERE cdm.course.srno = :courseId AND cdm.dropdown.type = :type")
    List<CourseDropdownMapping> findByCourseIdAndType(@Param("courseId") Integer courseId,
                                                      @Param("type") String type);
}