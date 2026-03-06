package com.example.sena_bhawan.entity.formation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_brigade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brigade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brigade_id")
    private Long brigadeId;

    @Column(name = "command_id", nullable = false)
    private Long commandId;

    @Column(name = "corps_id", nullable = false)
    private Long corpsId;

    @Column(name = "division_id", nullable = false)
    private Long divisionId;

    @Column(name = "brigade_name", nullable = false, length = 100)
    private String brigadeName;

    @Column(name = "location")
    private String location;

    @Column(name = "sus_no", unique = true)
    private String susNo;

    @Column(name = "pin_no")
    private String pinNo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "brig_code", length = 2)
    private String brigCode;

    @Column(name = "hq_id")
    private Long hqId;
}
