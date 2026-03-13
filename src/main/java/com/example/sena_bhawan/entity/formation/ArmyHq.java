package com.example.sena_bhawan.entity.formation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_army_hq")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArmyHq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hq_id")
    private Long hqId;

    @Column(name = "hq_name", nullable = false, length = 100, unique = true)
    private String hqName;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
