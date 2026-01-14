package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CourseDetailsRequestDTO;
import com.example.sena_bhawan.entity.CourseDetails;

import java.util.List;

public interface CourseDetailsService {

    CourseDetails addCourse(CourseDetailsRequestDTO dto);

    List<CourseDetails> getByPersonnel(Long personnelId);

    CourseDetails getOne(Long id);

    CourseDetails updateCourse(Long id, CourseDetailsRequestDTO dto);

    void deleteCourse(Long id);
}
