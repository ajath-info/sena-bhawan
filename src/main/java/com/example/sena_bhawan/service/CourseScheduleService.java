package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CourseScheduleSummaryResponse;
//import com.example.sena_bhawan.dto.CourseStep1Dto;
import com.example.sena_bhawan.dto.CourseStep1DTO;
import com.example.sena_bhawan.dto.CreateCourseScheduleRequest;
import com.example.sena_bhawan.dto.Step2PanelStrengthDTO;
import com.example.sena_bhawan.entity.CourseSchedule;

import java.util.List;

public interface CourseScheduleService {

    CourseSchedule addSchedule(CreateCourseScheduleRequest request);


    List<CourseSchedule> getAllSchedules();

    Step2PanelStrengthDTO getPanelStrength(Integer courseId);

    CourseSchedule getScheduleById(Long id);
    List<CourseStep1DTO> getStep1Courses();


    void deleteSchedule(Long id);

    // NEW: wrapper response with details + counts + list
    CourseScheduleSummaryResponse getCourseScheduleSummary(Integer courseId);

    List<CourseSchedule> getSchedulesByCourseId(Integer courseId);

}