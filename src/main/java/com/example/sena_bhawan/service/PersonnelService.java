package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.Personnel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonnelService {

    List<Personnel> getallPersonnels() ;

    Personnel getPersonnelById(Long id);

    Long createPersonnel(CreatePersonnelRequest request, MultipartFile image);

    void updateDecorations(Long id, List<DecorationRequest> decorations);

    void updateQualifications(Long id, List<QualificationRequest> req);

    void updateAdditionalQualifications(Long id, List<AdditionalQualificationRequest> req);

    void updateFamily(Long id, List<FamilyRequest> req);

    void updateMedical(Long id, MedicalUpdateRequest req);

    void updateBasicDetails(Long id, UpdatePersonnelRequest req);

    RankStrengthResponse getOfficerStrengthByRank();

    AgeBandResponse getAgeBandDistribution();

    MedicalCategoryResponse getMedicalCategoryDistribution();
}
