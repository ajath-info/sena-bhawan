package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "course_panel_batch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePanelBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId; // Reference to course schedule

    @Column(name = "movement_id", nullable = false)
    private Long movementId; // Reference to movement/approval process

    @Column(name = "status", length = 20)
    private String status; // Values: DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, FINALIZED

    @Column(name = "total_nominations")
    private Integer totalNominations; // Total number of nominations in this batch

    @Column(name = "created_by")
    private Long createdBy; // User ID who created the batch

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Timestamp when batch was created

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Timestamp when batch was last updated

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks; // Batch remarks

    // One-to-many relationship with nominations
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CoursePanelNomination> nominations;


}