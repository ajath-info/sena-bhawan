package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.AppointmentMaster;
import com.example.sena_bhawan.repository.AppointmentRepository;
import com.example.sena_bhawan.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public AppointmentMaster createAppointment(String appointmentName) {
        AppointmentMaster appointment = new AppointmentMaster();
        appointment.setAppointmentName(appointmentName);
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentMaster> getAllAppointments() {
        return appointmentRepository.findByIsActiveTrue();
    }

    @Override
    public AppointmentMaster updateAppointment(Long id, String appointmentName) {
        AppointmentMaster appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setAppointmentName(appointmentName);
        return appointmentRepository.save(appointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        AppointmentMaster appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setIsActive(false);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentMaster> getAllAppointment() {
        return List.of();
    }
}

