package com.example.sena_bhawan.entity;

//package com.example.sena_bhawan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_master")
public class AppointmentMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_name", nullable = false, unique = true)
    private String appointmentName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // ===== Constructors =====
    public AppointmentMaster() {}

    public AppointmentMaster(String appointmentName) {
        this.appointmentName = appointmentName;
    }

    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointmentName() {
        return appointmentName;
    }

    public void setAppointmentName(String appointmentName) {
        this.appointmentName = appointmentName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}

