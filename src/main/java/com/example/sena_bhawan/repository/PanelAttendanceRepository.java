package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PanelAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PanelAttendanceRepository extends JpaRepository<PanelAttendance, Long> {

    Optional<PanelAttendance> findByCourseIdAndScheduleIdAndPersonnelId(
            Integer courseId,
            Long scheduleId,
            Long personnelId
    );

    @Query(value = """
    SELECT p.id,
           p.full_name,
           p.army_no,
           p.rank,
           pd.unit_name,
           pd.command,
           p.date_of_seniority,
           COALESCE(pa.status, 'NOT_ATTENDING')
    FROM personnel p
    LEFT JOIN posting_details pd
           ON pd.personnel_id = p.id
    LEFT JOIN panel_attendance pa
           ON pa.personnel_id = p.id
          AND pa.course_id = :courseId
          AND pa.schedule_id = :scheduleId
""", nativeQuery = true)
    List<Object[]> findAttendance(
            @Param("courseId") Long courseId,
            @Param("scheduleId") Long scheduleId
    );

}

