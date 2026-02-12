package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CourseScheduleRoleStatusRequestDTO;
import com.example.sena_bhawan.dto.CourseScheduleRoleStatusResponseDTO;

import java.util.List;

public interface CourseScheduleRoleStatusService {

    void saveStatus(CourseScheduleRoleStatusRequestDTO request);

    List<CourseScheduleRoleStatusResponseDTO> getAll();

    List<CourseScheduleRoleStatusResponseDTO> getByScheduleId(Long scheduleId);
}
