package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseEligibilityDTO;
import com.example.sena_bhawan.service.CourseEligibilityService;
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
}

