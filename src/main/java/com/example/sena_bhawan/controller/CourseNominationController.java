package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseNominationDTO;
import com.example.sena_bhawan.service.CourseNominationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseNominationController {
    
    private final CourseNominationService courseNominationService;
    
    @GetMapping("/nominations")
    public ResponseEntity<Page<CourseNominationDTO>> getNominationsByCourseName(
            @RequestParam(required = false) String courseName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<CourseNominationDTO> response = courseNominationService.getNominationsByCourseName(courseName, page, size);
        return ResponseEntity.ok(response);
    }
}