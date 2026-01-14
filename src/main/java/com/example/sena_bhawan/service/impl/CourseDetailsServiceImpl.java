package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.CourseDetailsRequestDTO;
import com.example.sena_bhawan.entity.CourseDetails;
import com.example.sena_bhawan.entity.CourseMaster;
import com.example.sena_bhawan.repository.CourseDetailsRepository;
import com.example.sena_bhawan.repository.CourseMasterRepository;
import com.example.sena_bhawan.service.CourseDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseDetailsServiceImpl implements CourseDetailsService {

    private final CourseDetailsRepository repo;
    private final CourseMasterRepository courseMasterRepo;

    private String calculateDuration(LocalDate from, LocalDate to) {
        Period p = Period.between(from, to);
        int days = p.getYears() * 365 + p.getMonths() * 30 + p.getDays();
        int weeks = days / 7;
        return weeks + " Weeks";
    }

    @Override
    public CourseDetails addCourse(CourseDetailsRequestDTO dto) {

        CourseMaster cm = courseMasterRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseDetails cd = new CourseDetails();

        cd.setPersonnelId(dto.getPersonnelId());
        cd.setCourseId(dto.getCourseId());

        // AUTO POPULATE FROM COURSE_MASTER
        cd.setCourseName(cm.getCourseName());
        cd.setLocation(cm.getLocation());

        cd.setCourseSerialNo(dto.getCourseSerialNo());
        cd.setFromDate(dto.getFromDate());
        cd.setToDate(dto.getToDate());
        cd.setDuration(dto.getDuration());
        cd.setGrading(dto.getGrading());
        cd.setRemarks(dto.getRemarks());
        cd.setLetterNo(dto.getLetterNo());
        cd.setLetterDate(dto.getLetterDate());
        cd.setGradeCardPath(dto.getGradeCardPath());
        cd.setSupportingDocumentPath(dto.getSupportingDocumentPath());

        cd.setDuration(calculateDuration(dto.getFromDate(), dto.getToDate()));

        return repo.save(cd);
    }

    @Override
    public List<CourseDetails> getByPersonnel(Long personnelId) {
        return repo.findByPersonnelId(personnelId);
    }

    @Override
    public CourseDetails getOne(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public CourseDetails updateCourse(Long id, CourseDetailsRequestDTO dto) {

        CourseDetails cd = repo.findById(id).orElseThrow();

        CourseMaster cm = courseMasterRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        cd.setCourseId(dto.getCourseId());
        cd.setCourseName(cm.getCourseName());
        cd.setLocation(cm.getLocation());

        cd.setCourseSerialNo(dto.getCourseSerialNo());
        cd.setFromDate(dto.getFromDate());
        cd.setToDate(dto.getToDate());
        cd.setGrading(dto.getGrading());
        cd.setRemarks(dto.getRemarks());
        cd.setLetterNo(dto.getLetterNo());
        cd.setLetterDate(dto.getLetterDate());
        cd.setGradeCardPath(dto.getGradeCardPath());
        cd.setSupportingDocumentPath(dto.getSupportingDocumentPath());

        cd.setDuration(calculateDuration(dto.getFromDate(), dto.getToDate()));

        return repo.save(cd);
    }

    @Override
    public void deleteCourse(Long id) {
        repo.deleteById(id);
    }
}
