package com.example.sena_bhawan.repository;

//import com.example.sena_bhawan.dto.CourseStep1Dto;
import com.example.sena_bhawan.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    List<CourseSchedule> findByCourse_Srno(Integer srno);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.course.srno = :courseId ORDER BY cs.startDate DESC")
    List<CourseSchedule> findByCourseId(@Param("courseId") Integer courseId);

    @Query("""
        SELECT cs FROM CourseSchedule cs
        WHERE cs.startDate >= CURRENT_DATE
        ORDER BY cs.startDate
    """)
    List<CourseSchedule> findCurrentAndUpcoming();

    @Query("""
        SELECT cs FROM CourseSchedule cs
        WHERE cs.scheduleId = :scheduleId
        ORDER BY cs.startDate DESC
        """)
    CourseSchedule findByScheduleId(@Param("scheduleId") Integer scheduleId);

    // Count ongoing courses (current date between start_date and end_date)
    @Query("SELECT COUNT(c) FROM CourseSchedule c " +
            "WHERE :currentDate BETWEEN c.startDate AND c.endDate")
    long countOngoingCourses(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT cs FROM CourseSchedule cs LEFT JOIN FETCH cs.course WHERE cs.scheduleId = :scheduleId")
    Optional<CourseSchedule> findByIdWithCourse(@Param("scheduleId") Long scheduleId);
}


