package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "posting_details")
@Getter @Setter
public class PostingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "posting_id")
    private Long postingId;

    @Column(name = "personnel_id")
    private Long personnelId;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "location")
    private String location;

    @Column(name = "command")
    private String command;

    @Column(name = "appointment")
    private String appointment;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "duration")
    private String duration;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "document_path")
    private String documentPath;

    @Column(name = "movement_date")
    private LocalDate movementDate;

    @Column(name = "posted_to")
    private String postedTo;

    @Column(name = "posting_order_issue_date")
    private LocalDate postingOrderIssueDate;

    @Column(name = "tos_updated_date")
    private LocalDate tosUpdatedDate;

    @Column(name = "rank")
    private String rank;

    @Column(name = "status")
    private String status;

    @Column(name = "formation_type")
    private String formationType;
}
