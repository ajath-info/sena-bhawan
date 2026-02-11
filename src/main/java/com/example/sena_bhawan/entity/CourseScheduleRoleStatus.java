package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "course_schedule_role_status")
public class CourseScheduleRoleStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "send_date", nullable = false, insertable = false, updatable = false)
    private LocalDate sendDate; // handled by DB

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;


}
