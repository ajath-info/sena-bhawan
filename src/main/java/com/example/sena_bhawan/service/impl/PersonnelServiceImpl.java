package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.CreatePersonnelRequest;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.service.PersonnelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public List<Personnel> getallPersonnels() {
        return personnelRepository.findAll();
    }

    @Override
    public Personnel getPersonnelById(Long id) {
        return personnelRepository.findById(id).orElse(null);
    }


    @Override
    @Transactional
    public Long createPersonnel(CreatePersonnelRequest req) {

        Personnel p = new Personnel();

        // Basic Info
        p.setArmyNo(req.armyNo);
        p.setRank(req.rank);
        p.setFullName(req.fullName);
        p.setDateOfCommission(req.dateOfCommission);
        p.setDateOfSeniority(req.dateOfSeniority);
        p.setDateOfBirth(req.dateOfBirth);
        p.setPlaceOfBirth(req.placeOfBirth);
        p.setOfficerImage(req.officerImage);

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
//        p.setMedicalS(req.medicalS);
//        p.setMedicalH(req.medicalH);
//        p.setMedicalA(req.medicalA);
//        p.setMedicalP(req.medicalP);
//        p.setMedicalE(req.medicalE);

        p.setMedicalCategory(req.medicalCategory);


        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());

        // CHILD RELATIONS

        // Decorations
        p.setDecorations(
                req.decorations
                        .stream().map(d -> {
                    PersonnelDecorations deco = new PersonnelDecorations();
                    deco.setDecorationCategory(d.decorationCategory);
                    deco.setDecorationName(d.decorationName);
                    deco.setAwardDate(d.awardDate);
                    deco.setCitation(d.citation);
                    deco.setPersonnel(p);
                    deco.setCreatedAt(LocalDateTime.now());
                    return deco;
                }).collect(Collectors.toList())
        );

        // Qualifications
        p.setQualifications(
                req.qualifications.stream().map(q -> {
                    PersonnelQualifications pq = new PersonnelQualifications();
                    pq.setQualification(q.qualification);
                    pq.setInstitution(q.institution);
                    pq.setYearOfCompletion(q.yearOfCompletion);
                    pq.setGradePercentage(q.gradePercentage);
                    pq.setPersonnel(p);
                    pq.setCreatedAt(LocalDateTime.now());
                    return pq;
                }).collect(Collectors.toList())
        );

        // Additional Qualifications
        p.setAdditionalQualifications(
                req.additionalQualifications.stream().map(a -> {
                    PersonnelAdditionalQualifications aq = new PersonnelAdditionalQualifications();
                    aq.setQualification(a.qualification);
                    aq.setIssuingAuthority(a.issuingAuthority);
                    aq.setYear(a.year);
                    aq.setValidity(a.validity);
                    aq.setPersonnel(p);
                    aq.setCreatedAt(LocalDate.now());
                    return aq;
                }).collect(Collectors.toList())
        );

        // Family
        p.setFamilyMembers(
                req.family.stream().map(f -> {
                    PersonnelFamily fam = new PersonnelFamily();
                    fam.setName(f.name);
                    fam.setRelationship(f.relationship);
                    fam.setContactNumber(f.contactNumber);
                    fam.setPersonnel(p);
                    fam.setCreatedAt(LocalDateTime.now());
                    return fam;
                }).collect(Collectors.toList())
        );

        Personnel saved = personnelRepository.save(p); // cascade saves everything

        return saved.getId();
    }
}

