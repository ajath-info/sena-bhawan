package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.CourseRemarksRequest;
import com.example.sena_bhawan.entity.CourseRemarks;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.repository.CourseRemarksRepository;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.service.CourseRemarksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseRemarksServiceImpl implements CourseRemarksService {

    private final CourseRemarksRepository courseRemarksRepository;
    private final PersonnelRepository personnelRepository;

    // ================= CREATE =================
    @Override
    public void createRemark(Long personnelId, CourseRemarksRequest request) {

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() ->
                        new RuntimeException("Personnel not found with id: " + personnelId)
                );

        CourseRemarks remarks = new CourseRemarks();
        remarks.setPersonnel(personnel);
        remarks.setBeforeDetailmentStatus(request.getBeforeDetailmentStatus());
        remarks.setBeforeDetailmentReason(request.getBeforeDetailmentReason());
        remarks.setAfterDetailmentStatus(request.getAfterDetailmentStatus());
        remarks.setAfterDetailmentReason(request.getAfterDetailmentReason());
        remarks.setGeneralRemarks(request.getGeneralRemarks());

        courseRemarksRepository.save(remarks);
    }

    // ================= GET BY PERSONNEL =================
    @Override
    @Transactional(readOnly = true)
    public List<CourseRemarks> getRemarksByPersonnelId(Long personnelId) {
        return courseRemarksRepository.findByPersonnelId(personnelId);
    }

    // ================= UPDATE =================
    @Override
    public void updateRemark(Long remarkId, CourseRemarksRequest request) {

        CourseRemarks remarks = courseRemarksRepository.findById(remarkId)
                .orElseThrow(() ->
                        new RuntimeException("Course remark not found with id: " + remarkId)
                );

        remarks.setBeforeDetailmentStatus(request.getBeforeDetailmentStatus());
        remarks.setBeforeDetailmentReason(request.getBeforeDetailmentReason());
        remarks.setAfterDetailmentStatus(request.getAfterDetailmentStatus());
        remarks.setAfterDetailmentReason(request.getAfterDetailmentReason());
        remarks.setGeneralRemarks(request.getGeneralRemarks());
    }


    @Override
    @Transactional(readOnly = true)
    public CourseRemarks getRemarkById(Long remarkId) {
        return courseRemarksRepository.findById(remarkId)
                .orElseThrow(() ->
                        new RuntimeException("Course remark not found with id: " + remarkId)
                );
    }

}
