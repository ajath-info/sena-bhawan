package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.CourseMaster;
import com.example.sena_bhawan.repository.CourseMasterRepository;
import com.example.sena_bhawan.service.CourseMasterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseMasterServiceImpl implements CourseMasterService {

    private final CourseMasterRepository repository;

    public CourseMasterServiceImpl(CourseMasterRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CourseMaster> getAllCourses() {
        return repository.findAll();
    }

    @Override
    public long getCourseCount() {
        return repository.count();
    }

    @Override
    public CourseMaster getCourseById(Integer srno) {
        return repository.findById(srno)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public CourseMaster addCourse(CourseMaster course) {
        return repository.save(course);
    }

    @Override
    public CourseMaster updateCourse(Integer srno, CourseMaster updated) {

        CourseMaster existing = repository.findById(srno)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        existing.setCourseName(updated.getCourseName());
        existing.setLocation(updated.getLocation());
        existing.setDuration(updated.getDuration());

        return repository.save(existing);
    }

    public void delete(Integer srno) {
        repository.deleteById(srno);
    }

}
