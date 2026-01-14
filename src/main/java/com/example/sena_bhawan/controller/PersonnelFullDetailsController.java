package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseDetailsRequestDTO;
import com.example.sena_bhawan.dto.PersonnelFullDetailsDTO;
import com.example.sena_bhawan.dto.PostingDetailsDTO;
import com.example.sena_bhawan.service.CourseDetailsService;
import com.example.sena_bhawan.service.PostingDetailsService;
import com.example.sena_bhawan.entity.PostingDetails;
import com.example.sena_bhawan.entity.CourseDetails;


import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/personnel-full-details")
@RequiredArgsConstructor
public class PersonnelFullDetailsController {

    private final PostingDetailsService postingService;
    private final CourseDetailsService courseService;

    @PostMapping("/save")
    @Transactional
    public String saveAllDetails(@RequestBody PersonnelFullDetailsDTO dto) {

        Long personnelId = dto.getPersonnelId();

        if (dto.getPostingDetails() != null) {
            for (PostingDetailsDTO posting : dto.getPostingDetails()) {
                posting.setPersonnelId(personnelId);
                postingService.addPosting(posting);
            }
        }

        if (dto.getCourseDetails() != null) {
            for (CourseDetailsRequestDTO course : dto.getCourseDetails()) {
                course.setPersonnelId(personnelId);
                courseService.addCourse(course);
            }
        }

        return "All posting & course details saved successfully!";
    }

    @GetMapping("/{personnelId}")
    public PersonnelFullDetailsDTO getAllDetails(@PathVariable Long personnelId) {

        List<PostingDetails> postingList = postingService.getByPersonnel(personnelId);
        List<CourseDetails> courseList = courseService.getByPersonnel(personnelId);

        PersonnelFullDetailsDTO response = new PersonnelFullDetailsDTO();
        response.setPersonnelId(personnelId);

        response.setPostingDetails(
                postingList.stream().map(pd -> {
                    PostingDetailsDTO dto = new PostingDetailsDTO();
                    dto.setPostingId(pd.getPostingId());
                    dto.setUnitName(pd.getUnitName());
                    dto.setLocation(pd.getLocation());
                    dto.setCommand(pd.getCommand());
                    dto.setAppointment(pd.getAppointment());
                    dto.setFromDate(pd.getFromDate());
                    dto.setToDate(pd.getToDate());
                    dto.setRemarks(pd.getRemarks());
                    dto.setDocumentPath(pd.getDocumentPath());
                    dto.setDuration(pd.getDuration());
                    return dto;
                }).toList()
        );

        response.setCourseDetails(
                courseList.stream().map(cd -> {
                    CourseDetailsRequestDTO dto = new CourseDetailsRequestDTO();
                    dto.setCourseId(cd.getCourseId());
                    dto.setCourseName(cd.getCourseName());
                    dto.setCourseSerialNo(cd.getCourseSerialNo());
                    dto.setFromDate(cd.getFromDate());
                    dto.setToDate(cd.getToDate());
                    dto.setGrading(cd.getGrading());
                    dto.setRemarks(cd.getRemarks());
                    dto.setLetterNo(cd.getLetterNo());
                    dto.setLetterDate(cd.getLetterDate());
                    dto.setGradeCardPath(cd.getGradeCardPath());
                    dto.setSupportingDocumentPath(cd.getSupportingDocumentPath());
                    dto.setDuration(cd.getDuration());
                    dto.setPersonnelId(cd.getPersonnelId());
                    return dto;
                }).toList()
        );

        return response;
    }

}
