package com.example.sena_bhawan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter @Setter
public class RoleMaster {

    @Id
    @Column(name = "id")
    private Long roleId;

    @Column(name = "name")
    private String roleName;
}

