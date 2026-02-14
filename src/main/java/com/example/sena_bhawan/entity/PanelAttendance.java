package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "panel_attendance")
@Getter
@Setter
public class PanelAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer courseId;
    private Long scheduleId;

    private Long personnelId;

    @Column(name = "attendance_status")
    private String attendanceStatus; // ATTENDING / NOT_ATTENDING / STANDBY

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
