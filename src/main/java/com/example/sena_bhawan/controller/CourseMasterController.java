package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseDetailsDto;
import com.example.sena_bhawan.entity.CourseMaster;
import com.example.sena_bhawan.repository.CourseMasterRepository;
import com.example.sena_bhawan.service.CourseMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseMasterController {

    private final CourseMasterService service;
    private final CourseMasterRepository courseMasterRepository;

    // ---------------- LIST ALL COURSES (DTO) ----------------
    @GetMapping
    public List<CourseDetailsDto> getAll() {

        return courseMasterRepository.findAll()
                .stream()
                .map(c -> {
                    CourseDetailsDto dto = new CourseDetailsDto();
                    dto.setSrno(c.getSrno());
                    dto.setCourseName(c.getCourseName());
                    dto.setDuration(c.getDuration());
                    dto.setLocation(c.getLocation());
                    return dto;
                })
                .toList();
    }

    @GetMapping("/{srno}")
    public ResponseEntity<CourseMaster> getCourse(@PathVariable Integer srno) {
        CourseMaster course = service.getCourseById(srno);
        return ResponseEntity.ok(course);
    }

    // ---------------- GET COURSE COUNT ----------------
    @GetMapping("/count")
    public long getCourseCount() {
        return service.getCourseCount();
    }

    // ---------------- ADD NEW COURSE ----------------
    @PostMapping
    public CourseMaster add(@RequestBody CourseMaster course) {
        return service.addCourse(course);
    }

    // ---------------- UPDATE COURSE ----------------
    @PutMapping("/{srno}")
    public CourseMaster update(
            @PathVariable Integer srno,
            @RequestBody CourseMaster course
    ) {
        return service.updateCourse(srno, course);
    }

    @DeleteMapping("/{srno}")
    public String delete(@PathVariable Integer srno) {
        service.delete(srno);
        return "Course deleted successfully";
    }

}
