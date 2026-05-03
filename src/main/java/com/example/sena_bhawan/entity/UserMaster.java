package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_master")
@Getter
@Setter
public class UserMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    private String username;
    private String appointment;
    @JsonIgnore
    private String password;

    private String sosNo;
    @Column(name = "heirarchy_order")
    private Integer hierarchyOrder;
}

