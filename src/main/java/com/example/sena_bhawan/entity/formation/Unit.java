package com.example.sena_bhawan.entity.formation;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_unit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "command_id")
    private Long commandId;

    @Column(name = "corps_id")
    private Long corpsId;

    @Column(name = "division_id")
    private Long divisionId;

    @Column(name = "brigade_id")
    private Long brigadeId;

    @Column(name = "unit_name", unique = true)
    private String unitName;

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

    @Column(name = "unit_code", length = 3)
    private String unitCode;

}
