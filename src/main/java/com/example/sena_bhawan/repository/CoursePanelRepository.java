package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CoursePanelNomination;
import com.example.sena_bhawan.projection.AttendanceStatusProjection;
import com.example.sena_bhawan.projection.OngoingCoursesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoursePanelRepository
        extends JpaRepository<CoursePanelNomination, Long> {

    void deleteByScheduleId(Long scheduleId);

    List<CoursePanelNomination> findByScheduleId(Long scheduleId);

    @Query(value = "SELECT cm.course_name as courseName, COUNT(cpn.id) as officerCount " +
            "FROM course_panel_nomination cpn " +
            "JOIN course_schedule cs ON cpn.schedule_id = cs.schedule_id " +
            "JOIN course_master cm ON cs.course_id = cm.srno " +
            "WHERE cpn.attendance_status = 'ATTENDING' " +
            "AND cs.start_date <= CURRENT_DATE " +
            "AND cs.end_date >= CURRENT_DATE " +
            "GROUP BY cm.course_name", nativeQuery = true)
    List<OngoingCoursesProjection> getCurrentOngoingCoursesWithCounts();

    @Query(value = "SELECT cm.course_name as courseName, COUNT(cpn.id) as officerCount " +
            "FROM course_panel_nomination cpn, course_schedule cs, course_master cm " +
            "WHERE cpn.schedule_id = cs.schedule_id " +
            "AND cs.course_id = cm.srno " +
            "AND cpn.attendance_status = 'ATTENDING' " +
            "GROUP BY cm.course_name", nativeQuery = true)
    List<OngoingCoursesProjection> getOngoingCoursesWithCountsAlternative();

    @Query("SELECT cpn.attendanceStatus as attendanceStatus, COUNT(cpn) as count " +
            "FROM CoursePanelNomination cpn " +
            "GROUP BY cpn.attendanceStatus")
    List<AttendanceStatusProjection> getAttendanceStatusCounts();
}
