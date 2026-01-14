package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseScheduleSummaryResponse;
import com.example.sena_bhawan.dto.CreateCourseScheduleRequest;
import com.example.sena_bhawan.entity.CourseSchedule;
import com.example.sena_bhawan.service.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courseschedule")
@CrossOrigin("*")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService scheduleService;

    @PostMapping("/add")
    public CourseSchedule addSchedule(@RequestBody CreateCourseScheduleRequest request) {
        System.out.println("Received payload: " + request);
        return scheduleService.addSchedule(request);
    }

    @GetMapping("/all")
    public List<CourseSchedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public CourseSchedule getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id);
    }

    // existing simple list version (optional to keep)
    @GetMapping("/course/{courseId}")
    public List<CourseSchedule> getSchedulesByCourse(@PathVariable Integer courseId) {
        return scheduleService.getSchedulesByCourseId(courseId);
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