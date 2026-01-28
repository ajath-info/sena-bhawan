package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseScheduleSummaryResponse;
//import com.example.sena_bhawan.dto.CourseStep1Dto;
import com.example.sena_bhawan.dto.CourseStep1DTO;
import com.example.sena_bhawan.dto.CreateCourseScheduleRequest;
import com.example.sena_bhawan.dto.Step2PanelStrengthDTO;
import com.example.sena_bhawan.entity.CourseSchedule;
import com.example.sena_bhawan.service.CourseScheduleService;
//import com.example.sena_bhawan.service.impl.CourseScheduleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courseschedule")
@CrossOrigin("*")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService scheduleService;
    private final CourseScheduleService service;
    @Autowired
    private CourseScheduleService courseScheduleService;

    public CourseScheduleController(CourseScheduleService service) {
        this.service = service;
    }
//    private CourseScheduleServiceImpl service;
//    private CourseScheduleService service;

    @PostMapping("/add")
    public CourseSchedule addSchedule(@RequestBody CreateCourseScheduleRequest request) {
        System.out.println("Received payload: " + request);
        return scheduleService.addSchedule(request);
    }
    @GetMapping("/ping")
    public String ping() {
        return "SERVICE INJECTED SUCCESSFULLY";
    }


    @GetMapping("/step2/{courseId}")
    public ResponseEntity<Step2PanelStrengthDTO>
    getPanelStrength(@PathVariable Integer courseId) {

        return ResponseEntity.ok(
                service.getPanelStrength(courseId)
        );
    }


    @GetMapping("/all")
    public List<CourseSchedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

//    @GetMapping("/step1/courses")
//    public List<CourseStep1Dto> getStep1Courses() {
//        return service.getStep1Courses();
//    }



    @GetMapping("/{id}")
    public CourseSchedule getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id);
    }

    // existing simple list version (optional to keep)
    @GetMapping("/course/{courseId}")
    public List<CourseSchedule> getSchedulesByCourse(@PathVariable Integer courseId) {
        return scheduleService.getSchedulesByCourseId(courseId);
    }

    @GetMapping("/step1")
    public List<CourseStep1DTO> getStep1Courses() {
        return courseScheduleService.getStep1Courses();
    }

    // NEW: clean JSON summary
    @GetMapping("/course/{courseId}/summary")
    public CourseScheduleSummaryResponse getCourseScheduleSummary(@PathVariable Integer courseId) {
        return scheduleService.getCourseScheduleSummary(courseId);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "Schedule deleted successfully!";
    }
}