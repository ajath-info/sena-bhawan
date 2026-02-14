package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseScheduleRoleStatusRequestDTO;
import com.example.sena_bhawan.dto.CourseScheduleRoleStatusResponseDTO;
import com.example.sena_bhawan.service.CourseScheduleRoleStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-schedule-role-status")
public class CourseScheduleRoleStatusController {

    private final CourseScheduleRoleStatusService service;

    public CourseScheduleRoleStatusController(
            CourseScheduleRoleStatusService service) {
        this.service = service;
    }

    // POST
    @PostMapping
    public ResponseEntity<?> saveStatus(
            @RequestBody CourseScheduleRoleStatusRequestDTO request) {

        service.saveStatus(request);
        return ResponseEntity.ok(Map.of());
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<CourseScheduleRoleStatusResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET BY SCHEDULE ID
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<CourseScheduleRoleStatusResponseDTO>> getByScheduleId(
            @PathVariable Long scheduleId) {

        return ResponseEntity.ok(service.getByScheduleId(scheduleId));
    }
}
