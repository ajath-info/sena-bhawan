package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.*;
//import com.example.sena_bhawan.dto.CourseStep1Dto;
import com.example.sena_bhawan.entity.CourseSchedule;
import com.example.sena_bhawan.service.CourseScheduleService;
//import com.example.sena_bhawan.service.impl.CourseScheduleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courseschedule")
@CrossOrigin("*")
@Slf4j
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService scheduleService;
    private final CourseScheduleService service;
    @Autowired
    private CourseScheduleService courseScheduleService;

    @GetMapping("/batches/{courseId}")
    public ResponseEntity<List<CourseScheduleDto>> getBatchesByCourse(@PathVariable Long courseId) {
        log.info("Fetching batches for course ID: {}", courseId);
        List<CourseScheduleDto> batches = courseScheduleService.getBatchesByCourseId(courseId);
        return ResponseEntity.ok(batches);
    }

    public CourseScheduleController(CourseScheduleService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<BaseApiResponse<CourseSchedule>> addSchedule(@RequestBody CreateCourseScheduleRequest request) {
        System.out.println("Received payload: " + request);

        try {
            // Validate required fields
            if (request.getCourseId() == null || request.getYear() == null ||
                    request.getStartDate() == null || request.getEndDate() == null) {
                return ResponseEntity.badRequest()
                        .body(BaseApiResponse.badRequest("All required fields must be filled"));
            }

            CourseSchedule schedule = scheduleService.addSchedule(request);

            if (schedule == null) {
                return ResponseEntity.badRequest()
                        .body(BaseApiResponse.badRequest("Insufficient personnel strength. Cannot create schedule."));
            }

            return ResponseEntity.ok(BaseApiResponse.success(schedule, "Course schedule saved successfully"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(BaseApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseApiResponse.internalError("An error occurred while saving the schedule"));
        }
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