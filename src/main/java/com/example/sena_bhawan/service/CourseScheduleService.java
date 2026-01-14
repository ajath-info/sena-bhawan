package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CourseScheduleSummaryResponse;
import com.example.sena_bhawan.dto.CreateCourseScheduleRequest;
import com.example.sena_bhawan.entity.CourseSchedule;

import java.util.List;

public interface CourseScheduleService {

    CourseSchedule addSchedule(CreateCourseScheduleRequest request);

    List<CourseSchedule> getAllSchedules();

    CourseSchedule getScheduleById(Long id);

    List<CourseSchedule> getSchedulesByCourseId(Integer courseId);

    void deleteSchedule(Long id);

    // NEW: wrapper response with details + counts + list
    CourseScheduleSummaryResponse getCourseScheduleSummary(Integer courseId);
}