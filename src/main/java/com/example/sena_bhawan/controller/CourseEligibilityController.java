package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseEligibilityDTO;
import com.example.sena_bhawan.service.CourseEligibilityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
@RequiredArgsConstructor
public class CourseEligibilityController {

    private final CourseEligibilityService service;

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody CourseEligibilityDTO dto) {
        return ResponseEntity.ok(service.saveEligibility(dto));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getEligibilityByCourseId(@PathVariable Integer courseId) {
        try {
            CourseEligibilityDTO dto = service.getEligibilityByCourseId(courseId);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

