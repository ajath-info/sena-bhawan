package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.CourseMaster;
import com.example.sena_bhawan.entity.CourseSchedule;
import com.example.sena_bhawan.repository.CourseMasterRepository;
import com.example.sena_bhawan.repository.CourseScheduleRepository;
import com.example.sena_bhawan.service.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseScheduleServiceImpl implements CourseScheduleService {

    @Autowired
    private CourseScheduleRepository scheduleRepo;

    @Autowired
    private CourseMasterRepository courseRepo;

    @Override
    public CourseSchedule addSchedule(CreateCourseScheduleRequest request) {
        CourseMaster course = courseRepo.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Invalid Course ID"));

        CourseSchedule schedule = new CourseSchedule();
        schedule.setCourse(course);
        schedule.setYear(request.getYear());
        schedule.setBatchNumber(request.getBatchNumber());
        schedule.setStartDate(LocalDate.parse(request.getStartDate()));
        schedule.setEndDate(LocalDate.parse(request.getEndDate()));
        schedule.setCourseStrength(request.getCourseStrength());
        schedule.setVenue(request.getVenue());

        return scheduleRepo.save(schedule);
    }

    @Override
    public List<CourseSchedule> getAllSchedules() {
        return scheduleRepo.findAll();
    }

    @Override
    public CourseSchedule getScheduleById(Long id) {
        return scheduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    @Override
    public List<CourseSchedule> getSchedulesByCourseId(Integer courseId) {
        return scheduleRepo.findByCourse_Srno(courseId);
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleRepo.deleteById(id);
    }

    @Override
    public CourseScheduleSummaryResponse getCourseScheduleSummary(Integer courseId) {
        CourseMaster course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Invalid Course ID"));

        List<CourseSchedule> schedules = scheduleRepo.findByCourse_Srno(courseId);

        // ----- couseDetails -----
        CourseDetailsDto detailsDto = new CourseDetailsDto();
        detailsDto.setSrno(course.getSrno());
        detailsDto.setCourseName(course.getCourseName());
        detailsDto.setDuration(course.getDuration());
        detailsDto.setLocation(course.getLocation());

        // ----- couseCount -----
        LocalDate today = LocalDate.now();

        long totalSched = schedules.size();
        long currentBatch = schedules.stream()
                .filter(s -> !s.getStartDate().isAfter(today) && !s.getEndDate().isBefore(today))
                .count();
        long upcoming = schedules.stream()
                .filter(s -> s.getStartDate().isAfter(today))
                .count();

        CourseCountDto countDto = new CourseCountDto();
        countDto.setTotalschedule(totalSched);
        countDto.setCurrentbatch(currentBatch);
        countDto.setUpcoming(upcoming);

        // ----- courseschdule list -----
        List<CourseScheduleItemDto> itemDtos = schedules.stream()
                .map(s -> {
                    CourseScheduleItemDto dto = new CourseScheduleItemDto();
                    dto.setScheduleId(s.getScheduleId());
                    dto.setYear(s.getYear());
                    dto.setBatchNumber(s.getBatchNumber());
                    dto.setStartDate(s.getStartDate());
                    dto.setEndDate(s.getEndDate());
                    dto.setCourseStrength(s.getCourseStrength());
                    dto.setVenue(s.getVenue());
                    dto.setCourseId(course.getSrno()); // same course info for each
                    return dto;
                })
                .collect(Collectors.toList());

        // ----- wrapper -----
        CourseScheduleSummaryResponse response = new CourseScheduleSummaryResponse();
        response.setCourseDetails(detailsDto);
        response.setCourseCount(countDto);
        response.setCourseSchedule(itemDtos);

        return response;
    }
}