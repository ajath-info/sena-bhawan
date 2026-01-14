package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    // All schedules for a given course srno
    List<CourseSchedule> findByCourse_Srno(Integer srno);
}