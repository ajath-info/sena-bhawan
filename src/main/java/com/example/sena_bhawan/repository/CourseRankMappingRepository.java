package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseRankMapping;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRankMappingRepository extends JpaRepository<CourseRankMapping, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM CourseRankMapping crm WHERE crm.course.srno = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT crm.rank.id FROM CourseRankMapping crm WHERE crm.course.srno = :courseId")
    List<Long> findRankIdsByCourseId(@Param("courseId") Integer courseId);
}