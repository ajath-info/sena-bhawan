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

    private static final int BUFFER_SIZE = 10;




    @Autowired
    private CourseScheduleRepository repository;

    @Autowired
    private CourseMasterRepository courseRepo;
    private CourseScheduleRepository scheduleRepository;

    public CourseScheduleServiceImpl(
            CourseScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }





//    @Autowired
//    public CourseScheduleServiceImpl(
//            CourseScheduleRepository scheduleRepository) {
//        this.scheduleRepository = scheduleRepository;
//    }




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
    public Step2PanelStrengthDTO getPanelStrength(Integer courseId) {

        // Step-1 validation
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID is required");
        }

        // Latest / active schedule fetch
        List<CourseSchedule> schedules =
                scheduleRepository.findByCourseId(courseId);

        if (schedules.isEmpty()) {
            throw new RuntimeException(
                    "No course schedule found for courseId: " + courseId);
        }

        CourseSchedule schedule = schedules.get(0);

        // 🔴 course_strength STRING from DB
        String strengthStr = schedule.getCourseStrength();

        if (strengthStr == null || strengthStr.isBlank()) {
            throw new RuntimeException(
                    "Course strength not defined in schedule");
        }

        // ✅ Safe conversion
        int courseStrength;
        try {
            courseStrength = Integer.parseInt(strengthStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Invalid course strength value: " + strengthStr);
        }

        // Panel size logic
        int panelSize = courseStrength + BUFFER_SIZE;

        // DTO response
        Step2PanelStrengthDTO dto = new Step2PanelStrengthDTO();
        dto.setCourseId(courseId);
        dto.setCourseStrength(strengthStr); // STRING
        dto.setBuffer(BUFFER_SIZE);
        dto.setPanelSize(panelSize);

        return dto;
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
//    @Override
//    public List<CourseStep1Dto> fetchStep1Courses() {
//        return repository.fetchStep1Courses();   // ✅ CORRECT
//    }

//    @Override
//    public List<CourseStep1Dto> getStep1Data() {
//        return List.of();
//    }

    @Override
    public List<CourseSchedule> getSchedulesByCourseId(Integer courseId) {

        return scheduleRepo.findByCourse_Srno(courseId);

    }




    @Override
    public void deleteSchedule(Long id) {
        scheduleRepo.deleteById(id);
    }

    @Override
    public List<CourseStep1DTO> getStep1Courses() {

        List<CourseSchedule> schedules =
                scheduleRepository.findCurrentAndUpcoming();

        return schedules.stream()
                .map(cs -> new CourseStep1DTO(
                        cs.getCourse().getSrno(),
                        cs.getCourse().getCourseName(),
                        cs.getCourse().getLocation(),
                        cs.getStartDate(),
                        cs.getEndDate(),
                        cs.getVenue()
                ))
                .collect(Collectors.toList());
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

//    @Override
//    public List<CourseStep1Dto> getStep1Courses() {
//        return List.of();
//    }


}