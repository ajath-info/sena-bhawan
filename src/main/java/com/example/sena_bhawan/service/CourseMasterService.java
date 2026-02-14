package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.CourseMaster;
import java.util.List;

public interface CourseMasterService {

    List<CourseMaster> getAllCourses();

    long getCourseCount();

    CourseMaster getCourseById(Integer srno);

    CourseMaster addCourse(CourseMaster course);

    CourseMaster updateCourse(Integer srno, CourseMaster course);

    void delete(Integer srno);
}
