package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseDetailsRequestDTO;
import com.example.sena_bhawan.entity.CourseDetails;
import com.example.sena_bhawan.service.CourseDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-details")
@RequiredArgsConstructor
public class CourseDetailsController {

    private final CourseDetailsService service;

    // CREATE
    @PostMapping("/add")
    public CourseDetails create(@RequestBody CourseDetailsRequestDTO dto) {
        return service.addCourse(dto);
    }

    // GET ALL BY PERSONNEL
    @GetMapping("/personnel/{personnelId}")
    public List<CourseDetails> getByPersonnel(@PathVariable Long personnelId) {
        return service.getByPersonnel(personnelId);
    }


    // UPDATE
    @PutMapping("/{id}")
    public CourseDetails update(@PathVariable Long id, @RequestBody CourseDetailsRequestDTO dto) {
        return service.updateCourse(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteCourse(id);
        return "Course details deleted successfully.";
    }
}
