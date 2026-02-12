package com.example.sena_bhawan.entity;

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

    private String username;
    private String appointment;
    private String password;

    private String sosNo;
//    private String unitName;
}

