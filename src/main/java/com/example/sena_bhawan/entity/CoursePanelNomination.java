package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "course_panel_nomination",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"schedule_id", "personnel_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePanelNomination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "personnel_id", nullable = false)
    private Long personnelId;

    @Column(name = "attendance_status", nullable = false, length = 20)
    private String attendanceStatus; // ATTENDING / NOT_ATTENDING
}
