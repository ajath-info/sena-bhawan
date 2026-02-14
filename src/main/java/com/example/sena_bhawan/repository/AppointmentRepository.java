package com.example.sena_bhawan.repository;

//package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.AppointmentMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentMaster, Long> {

    List<AppointmentMaster> findByIsActiveTrue();
}

