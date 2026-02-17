package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CourseRemarksRequest;
import com.example.sena_bhawan.entity.CourseRemarks;

import java.util.List;

public interface CourseRemarksService {

    void createRemark(Long personnelId, CourseRemarksRequest request);

    CourseRemarks getRemarkById(Long remarkId);

    List<CourseRemarks> getRemarksByPersonnelId(Long personnelId);

    void updateRemark(Long remarkId, CourseRemarksRequest request);
}
