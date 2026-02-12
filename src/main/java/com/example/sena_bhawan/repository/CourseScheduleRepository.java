package com.example.sena_bhawan.repository;

//import com.example.sena_bhawan.dto.CourseStep1Dto;
import com.example.sena_bhawan.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    List<CourseSchedule> findByCourse_Srno(Integer srno);

    @Query("""
        SELECT cs FROM CourseSchedule cs
        WHERE cs.startDate >= CURRENT_DATE
        ORDER BY cs.startDate
    """)
    List<CourseSchedule> findCurrentAndUpcoming();

    @Query("""
        SELECT cs FROM CourseSchedule cs
        WHERE cs.course.srno = :courseId
        ORDER BY cs.startDate DESC
        """)
    List<CourseSchedule> findByCourseId(@Param("courseId") Integer courseId);
}


