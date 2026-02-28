package com.example.sena_bhawan.entity.formation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_division")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "division_id")
    private Long divisionId;

    @Column(name = "command_id")
    private Long commandId;

    @Column(name = "corps_id")
    private Long corpsId;

    @Column(name = "division_name", unique = true)
    private String divisionName;

    @Column(name = "location")
    private String location;

    @Column(name = "sus_no", unique = true)
    private String susNo;

    @Column(name = "pin_no")
    private String pinNo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "hq_id")
    private Long hqId;

    @Column(name = "div_code" ,length = 2)
    private String divCode;
}
