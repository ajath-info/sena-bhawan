package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rank_master")
@Setter
@Getter
public class RankMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rank;

    private int personnelCount;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // getters & setters
}
