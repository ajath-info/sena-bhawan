package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseRemarksRequest;
import com.example.sena_bhawan.entity.CourseRemarks;
import com.example.sena_bhawan.service.CourseRemarksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/course-remarks")
@RequiredArgsConstructor
public class CourseRemarksController {

    private final CourseRemarksService courseRemarksService;

    @PostMapping("/personnel/{personnelId}")
    public ResponseEntity<?> create(
            @PathVariable Long personnelId,
            @RequestBody CourseRemarksRequest request) {

        courseRemarksService.createRemark(personnelId, request);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Course remark added successfully"
        ));
    }

    @GetMapping("/personnel/{personnelId}")
    public List<CourseRemarks> getByPersonnel(
            @PathVariable Long personnelId) {

        return courseRemarksService.getRemarksByPersonnelId(personnelId);
    }

    @PutMapping("/{remarkId}")
    public ResponseEntity<?> update(
            @PathVariable Long remarkId,
            @RequestBody CourseRemarksRequest request) {

        courseRemarksService.updateRemark(remarkId, request);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Course remark updated successfully"
        ));
    }

    @GetMapping("/{remarkId}")
    public ResponseEntity<CourseRemarks> getById(
            @PathVariable Long remarkId) {

        return ResponseEntity.ok(
                courseRemarksService.getRemarkById(remarkId)
        );
    }

}
