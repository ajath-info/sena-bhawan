package com.example.sena_bhawan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "user_role_info")
@Getter @Setter
public class UserRoleInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long roleId;

    public UserRoleInfo(Object o, Long userId, Long roleId) {
    }

    public UserRoleInfo() {

    }
}

