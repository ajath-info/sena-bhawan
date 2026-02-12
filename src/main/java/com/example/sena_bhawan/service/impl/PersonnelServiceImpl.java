package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.CreatePersonnelRequest;
import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.service.PersonnelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonnelServiceImpl implements PersonnelService {

    private final PersonnelRepository personnelRepository;

    public PersonnelServiceImpl(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
    }

    // ================= COMMON =================
    private Personnel getPersonnel(Long id) {
        return personnelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel not found with id " + id));
    }

    @Override
    public List<Personnel> getallPersonnels() {
        return personnelRepository.findAll();
    }

    @Override
    public Personnel getPersonnelById(Long id) {
        return getPersonnel(id);
    }

    // ================= CREATE =================
    @Override
    @Transactional
    public Long createPersonnel(CreatePersonnelRequest req, MultipartFile officerImage) {

        Personnel p = new Personnel();

        // Basic Info
        p.setArmyNo(req.armyNo);
        p.setRank(req.rank);
        p.setFullName(req.fullName);
        p.setDateOfCommission(req.dateOfCommission);
        p.setDateOfSeniority(req.dateOfSeniority);
        p.setDateOfBirth(req.dateOfBirth);
        p.setPlaceOfBirth(req.placeOfBirth);

        if (officerImage != null && !officerImage.isEmpty()) {
            p.setOfficerImage(saveOfficerImage(officerImage));
        }

        // Service
        p.setNrs(req.nrs);
        p.setReligion(req.religion);
        p.setAadhaarNumber(req.aadhaarNumber);
        p.setPanCard(req.panCard);
        p.setMaritalStatus(req.maritalStatus);
        p.setCdaAccountNo(req.cdaAccountNo);

        // Address
        p.setPermanentAddress(req.permanentAddress);
        p.setCity(req.city);
        p.setDistrict(req.district);
        p.setState(req.state);
        p.setPinCode(req.pinCode);

        // Contact
        p.setMobileNumber(req.mobileNumber);
        p.setAlternateMobile(req.alternateMobile);
        p.setEmailAddress(req.emailAddress);

        // Medical
        p.setMedicalCategory(req.medicalCategory);
        p.setMedicalRemark(req.medicalRemark);

        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());

        // Decorations
        if (req.decorations != null) {
            p.setDecorations(req.decorations.stream().map(d -> {
                PersonnelDecorations deco = new PersonnelDecorations();
                deco.setDecorationCategory(d.decorationCategory);
                deco.setDecorationName(d.decorationName);
                deco.setAwardDate(d.awardDate);
                deco.setCitation(d.citation);
                deco.setPersonnel(p);
                deco.setCreatedAt(LocalDateTime.now());
                return deco;
            }).collect(Collectors.toList()));
        }

        // Qualifications
        if (req.qualifications != null) {
            p.setQualifications(req.qualifications.stream().map(q -> {
                PersonnelQualifications pq = new PersonnelQualifications();
                pq.setQualification(q.qualification);
                pq.setInstitution(q.institution);
                pq.setYearOfCompletion(q.yearOfCompletion);
                pq.setGradePercentage(q.gradePercentage);
                pq.setPersonnel(p);
                pq.setCreatedAt(LocalDateTime.now());
                return pq;
            }).collect(Collectors.toList()));
        }

        // Additional Qualifications
        if (req.additionalQualifications != null) {
            p.setAdditionalQualifications(req.additionalQualifications.stream().map(a -> {
                PersonnelAdditionalQualifications aq = new PersonnelAdditionalQualifications();
                aq.setQualification(a.qualification);
                aq.setIssuingAuthority(a.issuingAuthority);
                aq.setYear(a.year);
                aq.setValidity(a.validity);
                aq.setPersonnel(p);
                aq.setCreatedAt(LocalDate.now());
                return aq;
            }).collect(Collectors.toList()));
        }

        // Family
        if (req.family != null) {
            p.setFamilyMembers(req.family.stream().map(f -> {
                PersonnelFamily fam = new PersonnelFamily();
                fam.setName(f.name);
                fam.setRelationship(f.relationship);
                fam.setContactNumber(f.contactNumber);
                fam.setPersonnel(p);
                fam.setCreatedAt(LocalDateTime.now());
                return fam;
            }).collect(Collectors.toList()));
        }

        return personnelRepository.save(p).getId();
    }

    // ================= SECTION-WISE UPDATES =================
    @Override
    @Transactional
    public void updateBasicInfo(Long id, UpdateBasicInfoRequest req) {
        Personnel p = getPersonnel(id);
        p.setArmyNo(req.armyNo);
        p.setRank(req.rank);
        p.setFullName(req.fullName);
        p.setDateOfCommission(req.dateOfCommission);
        p.setDateOfSeniority(req.dateOfSeniority);
        p.setDateOfBirth(req.dateOfBirth);
        p.setPlaceOfBirth(req.placeOfBirth);
        p.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateServiceDetails(Long id, UpdateServiceRequest req) {
        Personnel p = getPersonnel(id);
        p.setNrs(req.nrs);
        p.setReligion(req.religion);
        p.setAadhaarNumber(req.aadhaarNumber);
        p.setPanCard(req.panCard);
        p.setMaritalStatus(req.maritalStatus);
        p.setCdaAccountNo(req.cdaAccountNo);
        p.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateAddress(Long id, UpdateAddressRequest req) {
        Personnel p = getPersonnel(id);
        p.setPermanentAddress(req.permanentAddress);
        p.setCity(req.city);
        p.setDistrict(req.district);
        p.setState(req.state);
        p.setPinCode(req.pinCode);
        p.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateContact(Long id, UpdateContactRequest req) {
        Personnel p = getPersonnel(id);
        p.setMobileNumber(req.mobileNumber);
        p.setAlternateMobile(req.alternateMobile);
        p.setEmailAddress(req.emailAddress);
        p.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateMedical(Long id, UpdateMedicalRequest req) {
        Personnel p = getPersonnel(id);
        p.setMedicalCategory(req.medicalCategory);
        p.setMedicalRemark(req.medicalRemark);
        p.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateDecorations(Long id, List<CreatePersonnelRequest.DecorationDTO> list) {

        Personnel p = getPersonnel(id);

        // Clear existing decorations (orphanRemoval deletes old rows)
        p.getDecorations().clear();

        if (list == null || list.isEmpty()) {
            p.setUpdatedAt(LocalDateTime.now());
            return;
        }

        list.forEach(d -> {
            PersonnelDecorations deco = new PersonnelDecorations();
            deco.setDecorationCategory(d.decorationCategory);
            deco.setDecorationName(d.decorationName);
            deco.setAwardDate(d.awardDate);
            deco.setCitation(d.citation);
            deco.setPersonnel(p);
            deco.setCreatedAt(LocalDateTime.now());
            p.getDecorations().add(deco);
        });

        p.setUpdatedAt(LocalDateTime.now());
    }


    @Override
    @Transactional
    public void updateQualifications(Long id, List<CreatePersonnelRequest.QualificationDTO> list) {

        Personnel p = getPersonnel(id);

        p.getQualifications().clear();

        if (list == null || list.isEmpty()) {
            p.setUpdatedAt(LocalDateTime.now());
            return;
        }

        list.forEach(q -> {
            PersonnelQualifications pq = new PersonnelQualifications();
            pq.setQualification(q.qualification);
            pq.setInstitution(q.institution);
            pq.setYearOfCompletion(q.yearOfCompletion);
            pq.setGradePercentage(q.gradePercentage);
            pq.setPersonnel(p);
            pq.setCreatedAt(LocalDateTime.now());
            p.getQualifications().add(pq);
        });

        p.setUpdatedAt(LocalDateTime.now());
    }


    @Override
    @Transactional
    public void updateAdditionalQualifications(
            Long id,
            List<CreatePersonnelRequest.AdditionalQualificationDTO> list) {

        Personnel p = getPersonnel(id);

        p.getAdditionalQualifications().clear();

        if (list == null || list.isEmpty()) {
            p.setUpdatedAt(LocalDateTime.now());
            return;
        }

        list.forEach(a -> {
            PersonnelAdditionalQualifications aq = new PersonnelAdditionalQualifications();
            aq.setQualification(a.qualification);
            aq.setIssuingAuthority(a.issuingAuthority);
            aq.setYear(a.year);
            aq.setValidity(a.validity);
            aq.setPersonnel(p);
            aq.setCreatedAt(LocalDate.now());
            p.getAdditionalQualifications().add(aq);
        });

        p.setUpdatedAt(LocalDateTime.now());
    }


    @Override
    @Transactional
    public void updateFamily(Long id, List<CreatePersonnelRequest.FamilyDTO> list) {

        Personnel p = getPersonnel(id);

        p.getFamilyMembers().clear();

        if (list == null || list.isEmpty()) {
            p.setUpdatedAt(LocalDateTime.now());
            return;
        }

        list.forEach(f -> {
            PersonnelFamily fam = new PersonnelFamily();
            fam.setName(f.name);
            fam.setRelationship(f.relationship);
            fam.setContactNumber(f.contactNumber);
            fam.setPersonnel(p);
            fam.setCreatedAt(LocalDateTime.now());
            p.getFamilyMembers().add(fam);
        });

        p.setUpdatedAt(LocalDateTime.now());
    }


    // ================= IMAGE =================
    @Override
    @Transactional
    public void updateOfficerImage(Long id, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Image is required");
        }
        Personnel p = getPersonnel(id);
        p.setOfficerImage(saveOfficerImage(image));
        p.setUpdatedAt(LocalDateTime.now());
    }

    // ================= FILE SAVE (ONLY ONCE) =================
    private String saveOfficerImage(MultipartFile file) {
        try {
            String folder = "uploads/personnel/officer/";
            File dir = new File(folder);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + fileName);
            Files.write(path, file.getBytes());

            return folder + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save officer image", e);
        }
    }
}
