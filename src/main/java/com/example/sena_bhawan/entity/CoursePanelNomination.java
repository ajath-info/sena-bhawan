package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private String attendanceStatus; // Reserve // Detailed

    // New columns for grading
    @Column(name = "grade", length = 10)
    private String grade;

    @Column(name = "instructor_award")
    private Boolean instructorAward;

    @Column(name = "grade_remarks", length = 500)
    private String gradeRemarks;

    @Column(name = "grade_status", length = 20)
    private String gradeStatus; // Graded / Pending

    @Column(name = "status", length = 20)
    private String status; // Values: ACTIVE, REMOVED, REPLACED, CONFIRMED

    // Audit columns
    @Column(name = "created_at")
    private LocalDateTime createdAt; // Timestamp when record was created

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Timestamp when record was last updated

    @Column(name = "created_by")
    private Long createdBy; // User ID who created the record

    @Column(name = "serial_number")
    private Long serialNumber; // User ID who created the record

    @Column(name = "updated_by")
    private Long updatedBy; // User ID who last updated the record

    @Column(name = "batch_id")
    private Long batchId; // Foreign key reference to course_panel_batch table

    // Relationships (optional - for JPA)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", insertable = false, updatable = false)
    private CoursePanelBatch batch; // Reference to parent batch

}
