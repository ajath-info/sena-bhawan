package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.CreatePersonnelRequest;
import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.projection.AgeBandProjection;
import com.example.sena_bhawan.projection.MedicalCategoryProjection;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.repository.PostingDetailsRepository;
import com.example.sena_bhawan.repository.UnitMasterRepository;
import com.example.sena_bhawan.service.PersonnelService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class PersonnelServiceImpl implements PersonnelService {

    private final UnitMasterRepository unitMasterRepository;
    private final PostingDetailsRepository postingDetailsRepository;
    private final PersonnelRepository personnelRepository;
    // STATIC labels in EXACT order as frontend
    private final List<String> STATIC_RANK_LABELS = Arrays.asList(
            "Lt", "Capt", "Maj", "Lt Col", "Col"
    );

    // STATIC labels in EXACT order as frontend
    private final List<String> STATIC_AGE_LABELS = Arrays.asList(
            "<30", "31-35", "36-40", "41-45", "46-50", "50+"
    );

    // Mapping of static labels to search terms
    private final Map<String, List<String>> RANK_SEARCH_TERMS = Map.of(
            "Lt", Arrays.asList("lt", "lieutenant"),
            "Capt", Arrays.asList("capt", "captain"),
            "Maj", Arrays.asList("maj", "major"),
            "Lt Col", Arrays.asList("lt col", "lieutenant col", "lt colonel", "lieutenant colonel"),
            "Col", Arrays.asList("col", "colonel")
    );

    @Override
    public MedicalCategoryResponse getMedicalCategoryDistribution() {
        List<MedicalCategoryProjection> projections =
                personnelRepository.getMedicalCategoryCounts();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (MedicalCategoryProjection projection : projections) {
            String category = projection.getMedicalCategory();
            if (category != null && !category.trim().isEmpty()) {
                labels.add(category.trim());
                data.add(projection.getCount().intValue());
            }
        }

        return MedicalCategoryResponse.builder()
                .labels(labels)
                .data(data)
                .chartType("doughnut")
                .title("Medical Category Distribution")
                .build();
    }

    @Override
    public RankStrengthResponse getOfficerStrengthByRank() {
        List<Integer> data = STATIC_RANK_LABELS.stream()
                .map(this::getCountForRank)
                .map(Long::intValue)
                .toList();

        return RankStrengthResponse.builder()
                .labels(STATIC_RANK_LABELS)
                .data(data)
                .chartType("bar")
                .title("Officer Strength by Rank")
                .build();
    }

    private long getCountForRank(String rankLabel) {
        List<String> searchTerms = RANK_SEARCH_TERMS.get(rankLabel);

        if (searchTerms == null || searchTerms.isEmpty()) {
            return personnelRepository.countByExactRank(rankLabel);
        }

        // Sum counts for all variations of this rank
        return searchTerms.stream()
                .mapToLong(term -> personnelRepository.countByRankContaining(term))
                .sum();
    }

    @Override
    public AgeBandResponse getAgeBandDistribution() {
        LocalDate now = LocalDate.now();
        LocalDate date30 = now.minusYears(30);
        LocalDate date35 = now.minusYears(35);
        LocalDate date40 = now.minusYears(40);
        LocalDate date45 = now.minusYears(45);
        LocalDate date50 = now.minusYears(50);

        AgeBandProjection projection = personnelRepository.getAllAgeBandCounts(
                date30, date35, date40, date45, date50
        );

        List<Integer> data = Arrays.asList(
                projection.getUnder30().intValue(),
                projection.getAge31to35().intValue(),
                projection.getAge36to40().intValue(),
                projection.getAge41to45().intValue(),
                projection.getAge46to50().intValue(),
                projection.getOver50().intValue()
        );

        return AgeBandResponse.builder()
                .labels(STATIC_AGE_LABELS)
                .data(data)
                .chartType("bar")
                .title("Age Band Distribution")
                .build();
    }

    public PersonnelServiceImpl(PersonnelRepository personnelRepository, UnitMasterRepository unitMasterRepository, PostingDetailsRepository postingDetailsRepository) {
        this.personnelRepository = personnelRepository;
        this.unitMasterRepository= unitMasterRepository;
        this.postingDetailsRepository=postingDetailsRepository;
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
        return personnelRepository.findById(id).orElse(null);
    }



    // ================= CREATE =================
    @Override
    @Transactional
    public Long createPersonnel(CreatePersonnelRequest req, MultipartFile officerImage) {

        Personnel p = new Personnel();

        // Basic Info
        p.setCommissionType(req.commissionType);
        p.setArmyNo(req.armyNo);
        p.setRank(req.rank);
        p.setFullName(req.fullName);
        p.setDateOfCommission(req.dateOfCommission);
        p.setDateOfSeniority(req.dateOfSeniority);
        p.setDateOfBirth(req.dateOfBirth);
        p.setPlaceOfBirth(req.placeOfBirth);

        if (officerImage != null && !officerImage.isEmpty()) {
            String imagePath = saveOfficerImage(officerImage);
            p.setOfficerImage(imagePath);

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
        p.setNsgEmail(req.nsgEmail);


        // Medical
//        p.setMedicalS(req.medicalS);
//        p.setMedicalH(req.medicalH);
//        p.setMedicalA(req.medicalA);
//        p.setMedicalP(req.medicalP);
//        p.setMedicalE(req.medicalE);

        // Medical
        p.setMedicalCategory(req.medicalCategory);
        p.setMedicalDate(req.medicalDate);
        p.setDiagnosis(req.diagnosis);
        p.setReviewDate(req.reviewDate);
        p.setRestriction(req.restriction);
        p.setInjuryCategory(req.injuryCategory);
        p.setIrsTransfer(req.irsTransfer);

        p.setMedicalRemark(req.medicalRemark);

        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());


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

//    @Override
//    @Transactional
//    public void updateMedical(Long id, UpdateMedicalRequest req) {
//        Personnel p = getPersonnel(id);
//        p.setMedicalCategory(req.medicalCategory);
//        p.setMedicalRemark(req.medicalRemark);
//        p.setUpdatedAt(LocalDateTime.now());
//    }

//    @Override
//    @Transactional
//    public void updateDecorations(Long id, List<CreatePersonnelRequest.DecorationDTO> list) {
//
//        Personnel p = getPersonnel(id);
//
//        Map<Long, PersonnelDecorations> existing =
//                p.getDecorations().stream()
//                        .collect(Collectors.toMap(
//                                PersonnelDecorations::getId,
//                                d -> d
//                        ));
//
//        if (list != null) {
//            for (CreatePersonnelRequest.DecorationDTO d : list) {
//
//                // UPDATE existing
//                if (d.id != null && existing.containsKey(d.id)) {
//                    PersonnelDecorations deco = existing.get(d.id);
//                    deco.setDecorationCategory(d.decorationCategory);
//                    deco.setDecorationName(d.decorationName);
//                    deco.setAwardDate(d.awardDate);
//                    deco.setCitation(d.citation);
//
//                    existing.remove(d.id);
//                }
//                // ADD new
//                else {
//>>>>>>> dev-karan
//                    PersonnelDecorations deco = new PersonnelDecorations();
//                    deco.setDecorationCategory(d.decorationCategory);
//                    deco.setDecorationName(d.decorationName);
//                    deco.setAwardDate(d.awardDate);
//                    deco.setCitation(d.citation);
//                    deco.setPersonnel(p);
//                    deco.setCreatedAt(LocalDateTime.now());
//
//                    p.getDecorations().add(deco);
//                }
//            }
//        }
//
//        // DELETE removed ones
//        existing.values().forEach(p.getDecorations()::remove);
//
//        p.setUpdatedAt(LocalDateTime.now());
//    }


//    @Override
//    @Transactional
//    public void updateQualifications(Long id, List<CreatePersonnelRequest.QualificationDTO> list) {
//
//        Personnel p = getPersonnel(id);
//
//        Map<Long, PersonnelQualifications> existing =
//                p.getQualifications().stream()
//                        .collect(Collectors.toMap(
//                                PersonnelQualifications::getId,
//                                q -> q
//                        ));
//
//        if (list != null) {
//            for (CreatePersonnelRequest.QualificationDTO q : list) {
//
//                if (q.id != null && existing.containsKey(q.id)) {
//                    PersonnelQualifications pq = existing.get(q.id);
//                    pq.setQualification(q.qualification);
//                    pq.setInstitution(q.institution);
//                    pq.setYearOfCompletion(q.yearOfCompletion);
//                    pq.setGradePercentage(q.gradePercentage);
//
//                    existing.remove(q.id);
//                } else {
//                    PersonnelQualifications pq = new PersonnelQualifications();
//                    pq.setQualification(q.qualification);
//                    pq.setStream(q.stream);
//                    pq.setInstitution(q.institution);
//                    pq.setYearOfCompletion(q.yearOfCompletion);
//                    pq.setGradePercentage(q.gradePercentage);
//                    pq.setPersonnel(p);
//                    pq.setCreatedAt(LocalDateTime.now());
//
//                    p.getQualifications().add(pq);
//                }
//            }
//        }
//
//        existing.values().forEach(p.getQualifications()::remove);
//
//        p.setUpdatedAt(LocalDateTime.now());
//    }

//
//    @Override
//    @Transactional
//    public void updateAdditionalQualifications(
//            Long id,
//            List<CreatePersonnelRequest.AdditionalQualificationDTO> list) {
//
//        Personnel p = getPersonnel(id);
//
//        Map<Long, PersonnelAdditionalQualifications> existing =
//                p.getAdditionalQualifications().stream()
//                        .collect(Collectors.toMap(
//                                PersonnelAdditionalQualifications::getId,
//                                a -> a
//                        ));
//
//        if (list != null) {
//            for (CreatePersonnelRequest.AdditionalQualificationDTO a : list) {
//
//                if (a.id != null && existing.containsKey(a.id)) {
//                    PersonnelAdditionalQualifications aq = existing.get(a.id);
//                    aq.setQualification(a.qualification);
//                    aq.setIssuingAuthority(a.issuingAuthority);
//                    aq.setYear(a.year);
//                    aq.setValidity(a.validity);
//
//                    existing.remove(a.id);
//                } else {
//                    PersonnelAdditionalQualifications aq =
//                            new PersonnelAdditionalQualifications();
//                    aq.setQualification(a.qualification);
//                    aq.setIssuingAuthority(a.issuingAuthority);
//                    aq.setYear(a.year);
//                    aq.setValidity(a.validity);
//                    aq.setPersonnel(p);
//                    aq.setCreatedAt(LocalDate.now());
//
//                    p.getAdditionalQualifications().add(aq);
//                }
//            }
//        }
//
//        existing.values().forEach(p.getAdditionalQualifications()::remove);
//
//        p.setUpdatedAt(LocalDateTime.now());
//    }



//    @Override
//    @Transactional
//    public void updateFamily(Long id, List<CreatePersonnelRequest.FamilyDTO> list) {
//
//        Personnel p = getPersonnel(id);
//
//        Map<Long, PersonnelFamily> existing =
//                p.getFamilyMembers().stream()
//                        .collect(Collectors.toMap(
//                                PersonnelFamily::getId,
//                                f -> f
//                        ));
//
//        if (list != null) {
//            for (CreatePersonnelRequest.FamilyDTO f : list) {
//
//                if (f.id != null && existing.containsKey(f.id)) {
//                    PersonnelFamily fam = existing.get(f.id);
//                    fam.setName(f.name);
//                    fam.setRelationship(f.relationship);
//                    fam.setContactNumber(f.contactNumber);
//
//                    existing.remove(f.id);
//                } else {
//                    PersonnelFamily fam = new PersonnelFamily();
//                    fam.setName(f.name);
//                    fam.setRelationship(f.relationship);
//                    fam.setContactNumber(f.contactNumber);
//                    fam.setPersonnel(p);
//                    fam.setCreatedAt(LocalDateTime.now());
//
//                    p.getFamilyMembers().add(fam);
//                }
//            }
//        }
//
//        existing.values().forEach(p.getFamilyMembers()::remove);
//
//        p.setUpdatedAt(LocalDateTime.now());
//    }

    private String saveOfficerImage(MultipartFile file) {

        try {
            String folder = "uploads/personnel/officer/";

            File dir = new File(folder);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName =
                    System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path path = Paths.get(folder + fileName);
            Files.write(path, file.getBytes());

            // return RELATIVE path (stored in DB)
            return folder + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save officer image", e);
        }
    }

    @Transactional
    public void updateDecorations(Long id, List<DecorationRequest> reqList) {

        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        List<PersonnelDecorations> existing = personnel.getDecorations();

        // Remove deleted decorations
        existing.removeIf(d ->
                reqList.stream().noneMatch(r -> r.id != null && r.id.equals(d.getId()))
        );

        // Update or Add
        for (DecorationRequest r : reqList) {

            if (r.id != null) {
                // UPDATE
                PersonnelDecorations deco = existing.stream()
                        .filter(d -> d.getId().equals(r.id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Decoration not found"));

                deco.setDecorationCategory(r.decorationCategory);
                deco.setDecorationName(r.decorationName);
                deco.setAwardDate(r.awardDate);
                deco.setCitation(r.citation);

            } else {
                // ADD NEW
                PersonnelDecorations deco = new PersonnelDecorations();
                deco.setDecorationCategory(r.decorationCategory);
                deco.setDecorationName(r.decorationName);
                deco.setAwardDate(r.awardDate);
                deco.setCitation(r.citation);
                deco.setPersonnel(personnel);
                deco.setCreatedAt(LocalDateTime.now());

                existing.add(deco);
            }
        }
    }

    @Transactional
    public void updateQualifications(Long personnelId, List<QualificationRequest> reqList) {

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        List<PersonnelQualifications> existing = personnel.getQualifications();

        // Remove deleted
        existing.removeIf(q ->
                reqList.stream().noneMatch(r -> r.id != null && r.id.equals(q.getId()))
        );

        for (QualificationRequest r : reqList) {

            if (r.id != null) {
                // UPDATE
                PersonnelQualifications q = existing.stream()
                        .filter(e -> e.getId().equals(r.id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Qualification not found"));

                q.setQualification(r.qualification);
                q.setStream(r.stream);
                q.setInstitution(r.institution);
                q.setYearOfCompletion(r.yearOfCompletion);
                q.setGradePercentage(r.gradePercentage);

            } else {
                // ADD NEW
                PersonnelQualifications q = new PersonnelQualifications();
                q.setQualification(r.qualification);
                q.setStream(r.stream);
                q.setInstitution(r.institution);
                q.setYearOfCompletion(r.yearOfCompletion);
                q.setGradePercentage(r.gradePercentage);
                q.setPersonnel(personnel);
                q.setCreatedAt(LocalDateTime.now());

                existing.add(q);
            }
        }
    }

    @Transactional
    public void updateAdditionalQualifications(
            Long personnelId,
            List<AdditionalQualificationRequest> reqList
    ) {

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        List<PersonnelAdditionalQualifications> existing =
                personnel.getAdditionalQualifications();

        // Remove deleted
        existing.removeIf(a ->
                reqList.stream().noneMatch(r -> r.id != null && r.id.equals(a.getId()))
        );

        for (AdditionalQualificationRequest r : reqList) {

            if (r.id != null) {
                // UPDATE
                PersonnelAdditionalQualifications aq = existing.stream()
                        .filter(e -> e.getId().equals(r.id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Additional qualification not found"));

                aq.setQualification(r.qualification);
                aq.setIssuingAuthority(r.issuingAuthority);
                aq.setYear(r.year);
                aq.setValidity(r.validity);

            } else {
                // ADD NEW
                PersonnelAdditionalQualifications aq =
                        new PersonnelAdditionalQualifications();

                aq.setQualification(r.qualification);
                aq.setIssuingAuthority(r.issuingAuthority);
                aq.setYear(r.year);
                aq.setValidity(r.validity);
                aq.setPersonnel(personnel);
                aq.setCreatedAt(LocalDate.now());

                existing.add(aq);
            }
        }
    }

    @Transactional
    public void updateSports(Long personnelId, List<SportsRequest> reqList) {

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        List<PersonnelSports> existing = personnel.getSports();

        // Remove deleted
        existing.removeIf(s ->
                reqList.stream().noneMatch(r -> r.id != null && r.id.equals(s.getId()))
        );

        for (SportsRequest r : reqList) {

            if (r.id != null) {
                // UPDATE
                PersonnelSports sport = existing.stream()
                        .filter(e -> e.getId().equals(r.id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Sport not found"));

                sport.setSportName(r.sportName);
                sport.setLevel(r.level);
                sport.setRemarks(r.remarks);

            } else {
                // ADD NEW
                PersonnelSports sport = new PersonnelSports();
                sport.setSportName(r.sportName);
                sport.setLevel(r.level);
                sport.setRemarks(r.remarks);

                sport.setPersonnel(personnel);
                existing.add(sport);
            }
        }
    }


    @Transactional
    public void updateFamily(Long personnelId, List<FamilyRequest> reqList) {

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        List<PersonnelFamily> existing = personnel.getFamilyMembers();

        // Remove deleted
        existing.removeIf(f ->
                reqList.stream().noneMatch(r -> r.id != null && r.id.equals(f.getId()))
        );

        for (FamilyRequest r : reqList) {

            if (r.id != null) {
                // UPDATE
                PersonnelFamily fam = existing.stream()
                        .filter(e -> e.getId().equals(r.id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Family member not found"));

                fam.setName(r.name);
                fam.setRelationship(r.relationship);
                fam.setContactNumber(r.contactNumber);

            } else {
                // ADD NEW
                PersonnelFamily fam = new PersonnelFamily();
                fam.setName(r.name);
                fam.setRelationship(r.relationship);
                fam.setContactNumber(r.contactNumber);
                fam.setPersonnel(personnel);
                fam.setCreatedAt(LocalDateTime.now());

                existing.add(fam);
            }
        }
    }

    @Transactional
    public void updateMedical(Long id, MedicalUpdateRequest req) {

        Personnel p = personnelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        p.setMedicalCategory(req.medicalCategory);
        p.setMedicalDate(req.medicalDate);
        p.setDiagnosis(req.diagnosis);
        p.setReviewDate(req.reviewDate);
        p.setRestriction(req.restriction);
        p.setInjuryCategory(req.injuryCategory);

        p.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void updateBasicDetails(Long id, UpdatePersonnelRequest req) {

        Personnel p = personnelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        p.setCommissionType(req.commissionType);
        p.setRank(req.rank);
        p.setFullName(req.fullName);
        p.setReligion(req.religion);
        p.setMaritalStatus(req.maritalStatus);
        p.setEmailAddress(req.emailAddress);
        p.setNsgEmail(req.nsgEmail);
        p.setMobileNumber(req.mobileNumber);
        p.setAlternateMobile(req.alternateMobile);

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

//    // ================= FILE SAVE (ONLY ONCE) =================
//    private String saveOfficerImage(MultipartFile file) {
//        try {
//            String folder = "uploads/personnel/officer/";
//            File dir = new File(folder);
//            if (!dir.exists()) dir.mkdirs();
//
//            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//            Path path = Paths.get(folder + fileName);
//            Files.write(path, file.getBytes());
//
//            return folder + fileName;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to save officer image", e);
//        }
//    }


    @Override
    @Transactional(readOnly = true)
    public List<Personnel> filterPersonnel(PersonnelFilterRequest f) {

        return personnelRepository.findAll((root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ================= RANK =================
            if (f.rank != null && !f.rank.isBlank()) {
                predicates.add(cb.equal(root.get("rank"), f.rank));
            }

            // ================= DOB (Greater Than) =================
            if (f.dobGreaterThan != null) {
                predicates.add(
                        cb.greaterThan(root.get("dateOfBirth"), f.dobGreaterThan)
                );
            }

            // ================= DATE OF COMMISSION =================
            if (f.docFrom != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("dateOfCommission"), f.docFrom)
                );
            }
            if (f.docTo != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("dateOfCommission"), f.docTo)
                );
            }

            // ================= DATE OF SENIORITY =================
            if (f.dosFrom != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("dateOfSeniority"), f.dosFrom)
                );
            }
            if (f.dosTo != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("dateOfSeniority"), f.dosTo)
                );
            }

            // ================= MEDICAL CATEGORY =================
            if (f.medicalCategory != null && !f.medicalCategory.isBlank()) {
                predicates.add(cb.equal(root.get("medicalCategory"), f.medicalCategory));
            }

            // ================= PLACE OF BIRTH =================
            if (f.placeOfBirth != null && !f.placeOfBirth.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("placeOfBirth")),
                                "%" + f.placeOfBirth.toLowerCase() + "%"
                        )
                );
            }

            // ================= FUTURE: ORGANISATION =================
            if (f.command != null && hasField(root, "command")) {
                predicates.add(cb.equal(root.get("command"), f.command));
            }

            if (f.corps != null && hasField(root, "corps")) {
                predicates.add(cb.equal(root.get("corps"), f.corps));
            }

            if (f.division != null && hasField(root, "division")) {
                predicates.add(cb.equal(root.get("division"), f.division));
            }

            if (f.establishmentType != null && hasField(root, "establishmentType")) {
                predicates.add(cb.equal(root.get("establishmentType"), f.establishmentType));
            }

            // ================= FUTURE: TOS =================
            if (f.tosFrom != null && hasField(root, "tosFrom")) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("tosFrom"), f.tosFrom)
                );
            }

            if (f.tosTo != null && hasField(root, "tosTo")) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("tosTo"), f.tosTo)
                );
            }

            // ================= FUTURE: COURSE =================
            if (f.courseName != null && hasField(root, "courseName")) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("courseName")),
                                "%" + f.courseName.toLowerCase() + "%"
                        )
                );
            }

            if (f.courseFrom != null && hasField(root, "courseFrom")) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("courseFrom"), f.courseFrom)
                );
            }

            if (f.courseTo != null && hasField(root, "courseTo")) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("courseTo"), f.courseTo)
                );
            }

            // ================= FUTURE: AREA / QUAL / SPORTS =================
            if (f.areaType != null && hasField(root, "areaType")) {
                predicates.add(cb.equal(root.get("areaType"), f.areaType));
            }

            if (f.civilQualification != null && hasField(root, "civilQualification")) {
                predicates.add(cb.equal(root.get("civilQualification"), f.civilQualification));
            }

            if (f.sports != null && hasField(root, "sports")) {
                predicates.add(cb.equal(root.get("sports"), f.sports));
            }

            // ================= FUTURE: POSTING DUE =================
            if (f.postingDueMonths != null && hasField(root, "postingDueDate")) {
                LocalDate dueDate = LocalDate.now().plusMonths(f.postingDueMonths);
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("postingDueDate"), dueDate)
                );
            }

            query.orderBy(cb.asc(root.get("rank")));
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }


    private boolean hasField(Root<Personnel> root, String fieldName) {
        try {
            root.get(fieldName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }



    @Override
    public OfficerSummaryDTO getOfficerSummaryByUnit(Long unitId) {

        List<Personnel> personnelList = getPersonnelByUnit(unitId);

        if (personnelList.isEmpty()) {
            return new OfficerSummaryDTO(0,0,0,0,null,null,null,null);
        }

        int totalOfficers = personnelList.size();
        int totalCoursesDone = 0;
        int coursesTrainingYr = 0;
        int coursesInUnit = 0;

        for (int i = 0; i < totalOfficers; i++) {
            totalCoursesDone += random(1, 4);
            coursesTrainingYr += random(0, 1);
            coursesInUnit += random(1, 2);
        }

        return new OfficerSummaryDTO(
                totalOfficers,
                totalCoursesDone,
                coursesTrainingYr,
                coursesInUnit,
                minDate(personnelList, true),
                maxDate(personnelList, true),
                minDate(personnelList, false),
                maxDate(personnelList, false)
        );
    }

    // ================= TABLE =================

    @Override
    public List<OfficerTableDTO> getOfficerTableByUnit(Long unitId) {

        List<Personnel> personnelList = getPersonnelByUnit(unitId);
        List<OfficerTableDTO> response = new ArrayList<>();

        for (Personnel p : personnelList) {
            response.add(new OfficerTableDTO(
                    p.getArmyNo(),
                    p.getRank(),
                    p.getFullName(),
                    randomGender(),
                    p.getDateOfBirth(),
                    p.getDateOfCommission(),
                    p.getDateOfSeniority(),
                    random(1,4),
                    random(0,1),
                    random(1,2)
            ));
        }
        return response;
    }

    // ================= COMMON =================

    private List<Personnel> getPersonnelByUnit(Long unitId) {

        UnitMaster unit = unitMasterRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        List<Long> ids =
                postingDetailsRepository
                        .findActivePersonnelIdsByUnitName(unit.getUnitName());

        if (ids.isEmpty()) return Collections.emptyList();

        return personnelRepository.findByIdIn(ids);
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private String randomGender() {
        return ThreadLocalRandom.current().nextBoolean() ? "M" : "F";
    }

    private LocalDate minDate(List<Personnel> list, boolean seniority) {
        return list.stream()
                .map(p -> seniority ? p.getDateOfSeniority()
                        : p.getDateOfCommission())
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    private LocalDate maxDate(List<Personnel> list, boolean seniority) {
        return list.stream()
                .map(p -> seniority ? p.getDateOfSeniority()
                        : p.getDateOfCommission())
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

}
