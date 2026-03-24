package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.ParamountDTO.*;

import java.util.List;

public interface PersonnelProfileService {

    IdentityAndServiceDto getIdentityAndService(Long personnelId);

    List<PostingHistoryDto> getPostingHistory(Long personnelId);

    List<CourseCompletedDto> getCoursesCompleted(Long personnelId);

    List<DecorationDto> getDecorations(Long personnelId);

    List<QualificationDto> getQualifications(Long personnelId);

    List<AdditionalQualificationDto> getAdditionalQualifications(Long personnelId);

    List<FamilyMemberDto> getFamilyMembers(Long personnelId);

    DisciplineAndMedicalDto getDisciplineAndMedical(Long personnelId);
}
