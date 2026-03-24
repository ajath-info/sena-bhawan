package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CoursePanelNomination;
import com.example.sena_bhawan.projection.AttendanceStatusProjection;
import com.example.sena_bhawan.projection.OngoingCoursesProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoursePanelRepository
        extends JpaRepository<CoursePanelNomination, Long> {

    // Find nomination by scheduleId and personnelId
    Optional<CoursePanelNomination> findByScheduleIdAndPersonnelId(Long scheduleId, Long personnelId);

    // Check if exists
    boolean existsByScheduleIdAndPersonnelId(Long scheduleId, Long personnelId);

    // Find all nominations for given personnel IDs
    List<CoursePanelNomination> findByPersonnelIdIn(List<Long> personnelIds);

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

    @Modifying
    @Transactional
    @Query("UPDATE CoursePanelNomination cpn SET cpn.grade = :grade, cpn.instructorAward = :instructorAward, cpn.gradeRemarks = :remarks, cpn.gradeStatus = :gradeStatus WHERE cpn.scheduleId = :scheduleId AND cpn.personnelId = :personnelId")
    int updateGradeDetails(@Param("scheduleId") Long scheduleId,
                           @Param("personnelId") Long personnelId,
                           @Param("grade") String grade,
                           @Param("instructorAward") Boolean instructorAward,
                           @Param("remarks") String remarks,
                           @Param("gradeStatus") String gradeStatus);

    @Query("SELECT COUNT(cpn) FROM CoursePanelNomination cpn WHERE cpn.scheduleId = :scheduleId")
    int countByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT cpn FROM CoursePanelNomination cpn " +
            "WHERE cpn.scheduleId = :scheduleId AND cpn.gradeStatus = 'Pending'")
    List<CoursePanelNomination> findPendingGradesByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT CASE WHEN COUNT(cpn) = 0 THEN true " +
            "ELSE COUNT(CASE WHEN cpn.gradeStatus = 'Pending' THEN 1 END) = 0 END " +
            "FROM CoursePanelNomination cpn WHERE cpn.scheduleId = :scheduleId")
    boolean isAllGraded(@Param("scheduleId") Long scheduleId);
}
