package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseScheduleRoleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseScheduleRoleStatusRepository
        extends JpaRepository<CourseScheduleRoleStatus, Long> {

    boolean existsByScheduleIdAndRoleId(Long scheduleId, Long roleId);

    List<CourseScheduleRoleStatus> findByScheduleId(Long scheduleId);
}
