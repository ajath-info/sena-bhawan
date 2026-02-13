package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.Personnel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonnelService {

    List<Personnel> getallPersonnels() ;

    Personnel getPersonnelById(Long id);

    Long createPersonnel(CreatePersonnelRequest request, MultipartFile image);

    void updateBasicInfo(Long id, UpdateBasicInfoRequest req);

    void updateServiceDetails(Long id, UpdateServiceRequest req);

    void updateAddress(Long id, UpdateAddressRequest req);

    void updateContact(Long id, UpdateContactRequest req);

    void updateMedical(Long id, UpdateMedicalRequest req);

    void updateDecorations(Long id, List<CreatePersonnelRequest.DecorationDTO> list);

    void updateQualifications(Long id, List<CreatePersonnelRequest.QualificationDTO> list);

    void updateAdditionalQualifications(Long id, List<CreatePersonnelRequest.AdditionalQualificationDTO> list);

    void updateFamily(Long id, List<CreatePersonnelRequest.FamilyDTO> list);

    void updateOfficerImage(Long id, MultipartFile image);

    List<Personnel> filterPersonnel(PersonnelFilterRequest filter);


}
