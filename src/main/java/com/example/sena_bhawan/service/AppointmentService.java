package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.AppointmentMaster;
import com.example.sena_bhawan.entity.RoleMaster;

import java.util.List;

public interface AppointmentService {
    AppointmentMaster createAppointment(String appointmentName);

    List<AppointmentMaster> getAllAppointments();

    AppointmentMaster updateAppointment(Long id, String appointmentName);

    void deleteAppointment(Long id);

    List<AppointmentMaster> getAllAppointment();
}
