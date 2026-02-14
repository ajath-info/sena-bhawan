package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.CourseScheduleRoleStatusRequestDTO;
import com.example.sena_bhawan.dto.CourseScheduleRoleStatusResponseDTO;
import com.example.sena_bhawan.entity.CourseScheduleRoleStatus;
import com.example.sena_bhawan.repository.CourseScheduleRoleStatusRepository;
import com.example.sena_bhawan.service.CourseScheduleRoleStatusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseScheduleRoleStatusServiceImpl
        implements CourseScheduleRoleStatusService {

    private final CourseScheduleRoleStatusRepository repository;

    public CourseScheduleRoleStatusServiceImpl(
            CourseScheduleRoleStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveStatus(CourseScheduleRoleStatusRequestDTO request) {

        CourseScheduleRoleStatus entity = new CourseScheduleRoleStatus();
        entity.setScheduleId(request.getScheduleId());
        entity.setRoleId(request.getRoleId());
        entity.setStatus(request.getStatus());
        entity.setRemark(request.getRemark());

        repository.save(entity);
    }

    @Override
    public List<CourseScheduleRoleStatusResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseScheduleRoleStatusResponseDTO> getByScheduleId(Long scheduleId) {
        return repository.findByScheduleId(scheduleId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CourseScheduleRoleStatusResponseDTO mapToDto(
            CourseScheduleRoleStatus entity) {

        CourseScheduleRoleStatusResponseDTO dto =
                new CourseScheduleRoleStatusResponseDTO();

        dto.setId(entity.getId());
        dto.setScheduleId(entity.getScheduleId());
        dto.setRoleId(entity.getRoleId());
        dto.setSendDate(entity.getSendDate());
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getRemark());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}
