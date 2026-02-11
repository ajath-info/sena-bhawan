package com.example.sena_bhawan.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "officer_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficerPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Officer Identity (MISSING FIELDS FIXED) =====
    @Column(name = "officer_army_no", length = 50)
    private String officerArmyNo;

    @Column(name = "officer_name",  length = 100)
    private String officerName;

    // ===== Officer =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private Personnel personnel;

    // ===== Under Posting =====
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate;

    @Column(name = "posted_to_unit", nullable = false, length = 150)
    private String postedToUnit;

    @ManyToOne
    @JoinColumn(name = "appointment")
    private AppointmentMaster appointment;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "posting_order_issue_date")
    private LocalDate postingOrderIssueDate;

    @ManyToOne
    @JoinColumn(name = "posting_type")
    private PostingTypeMaster postingType;

    // ===== Posting In =====
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tos_update_date")
    private LocalDate tosUpdateDate;

    @ManyToOne
    @JoinColumn(name = "rank_on_promotion")
    private RankMaster rankOnPromotion;

    // ===== Status =====
    @Column(name = "posting_status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void setAppointment(String appointment) {
    }

    public void setPostingType(String postingType) {
    }

    public void setRankOnPromotion(String rankOnPromotion) {
    }
}
