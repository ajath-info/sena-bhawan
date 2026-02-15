package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.CreatePersonnelRequest;
import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.PersonnelRepository;
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
import java.util.List;
import java.util.Map;
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

        Map<Long, PersonnelDecorations> existing =
                p.getDecorations().stream()
                        .collect(Collectors.toMap(
                                PersonnelDecorations::getId,
                                d -> d
                        ));

        if (list != null) {
            for (CreatePersonnelRequest.DecorationDTO d : list) {

                // UPDATE existing
                if (d.id != null && existing.containsKey(d.id)) {
                    PersonnelDecorations deco = existing.get(d.id);
                    deco.setDecorationCategory(d.decorationCategory);
                    deco.setDecorationName(d.decorationName);
                    deco.setAwardDate(d.awardDate);
                    deco.setCitation(d.citation);

                    existing.remove(d.id);
                }
                // ADD new
                else {
                    PersonnelDecorations deco = new PersonnelDecorations();
                    deco.setDecorationCategory(d.decorationCategory);
                    deco.setDecorationName(d.decorationName);
                    deco.setAwardDate(d.awardDate);
                    deco.setCitation(d.citation);
                    deco.setPersonnel(p);
                    deco.setCreatedAt(LocalDateTime.now());

                    p.getDecorations().add(deco);
                }
            }
        }

        // DELETE removed ones
        existing.values().forEach(p.getDecorations()::remove);

        p.setUpdatedAt(LocalDateTime.now());
    }


    @Override
    @Transactional
    public void updateQualifications(Long id, List<CreatePersonnelRequest.QualificationDTO> list) {

        Personnel p = getPersonnel(id);

        Map<Long, PersonnelQualifications> existing =
                p.getQualifications().stream()
                        .collect(Collectors.toMap(
                                PersonnelQualifications::getId,
                                q -> q
                        ));

        if (list != null) {
            for (CreatePersonnelRequest.QualificationDTO q : list) {

                if (q.id != null && existing.containsKey(q.id)) {
                    PersonnelQualifications pq = existing.get(q.id);
                    pq.setQualification(q.qualification);
                    pq.setInstitution(q.institution);
                    pq.setYearOfCompletion(q.yearOfCompletion);
                    pq.setGradePercentage(q.gradePercentage);

                    existing.remove(q.id);
                } else {
                    PersonnelQualifications pq = new PersonnelQualifications();
                    pq.setQualification(q.qualification);
                    pq.setInstitution(q.institution);
                    pq.setYearOfCompletion(q.yearOfCompletion);
                    pq.setGradePercentage(q.gradePercentage);
                    pq.setPersonnel(p);
                    pq.setCreatedAt(LocalDateTime.now());

                    p.getQualifications().add(pq);
                }
            }
        }

        existing.values().forEach(p.getQualifications()::remove);

        p.setUpdatedAt(LocalDateTime.now());
    }


    @Override
    @Transactional
    public void updateAdditionalQualifications(
            Long id,
            List<CreatePersonnelRequest.AdditionalQualificationDTO> list) {

        Personnel p = getPersonnel(id);

        Map<Long, PersonnelAdditionalQualifications> existing =
                p.getAdditionalQualifications().stream()
                        .collect(Collectors.toMap(
                                PersonnelAdditionalQualifications::getId,
                                a -> a
                        ));

        if (list != null) {
            for (CreatePersonnelRequest.AdditionalQualificationDTO a : list) {

                if (a.id != null && existing.containsKey(a.id)) {
                    PersonnelAdditionalQualifications aq = existing.get(a.id);
                    aq.setQualification(a.qualification);
                    aq.setIssuingAuthority(a.issuingAuthority);
                    aq.setYear(a.year);
                    aq.setValidity(a.validity);

                    existing.remove(a.id);
                } else {
                    PersonnelAdditionalQualifications aq =
                            new PersonnelAdditionalQualifications();
                    aq.setQualification(a.qualification);
                    aq.setIssuingAuthority(a.issuingAuthority);
                    aq.setYear(a.year);
                    aq.setValidity(a.validity);
                    aq.setPersonnel(p);
                    aq.setCreatedAt(LocalDate.now());

                    p.getAdditionalQualifications().add(aq);
                }
            }
        }

        existing.values().forEach(p.getAdditionalQualifications()::remove);

        p.setUpdatedAt(LocalDateTime.now());
    }



    @Override
    @Transactional
    public void updateFamily(Long id, List<CreatePersonnelRequest.FamilyDTO> list) {

        Personnel p = getPersonnel(id);

        Map<Long, PersonnelFamily> existing =
                p.getFamilyMembers().stream()
                        .collect(Collectors.toMap(
                                PersonnelFamily::getId,
                                f -> f
                        ));

        if (list != null) {
            for (CreatePersonnelRequest.FamilyDTO f : list) {

                if (f.id != null && existing.containsKey(f.id)) {
                    PersonnelFamily fam = existing.get(f.id);
                    fam.setName(f.name);
                    fam.setRelationship(f.relationship);
                    fam.setContactNumber(f.contactNumber);

                    existing.remove(f.id);
                } else {
                    PersonnelFamily fam = new PersonnelFamily();
                    fam.setName(f.name);
                    fam.setRelationship(f.relationship);
                    fam.setContactNumber(f.contactNumber);
                    fam.setPersonnel(p);
                    fam.setCreatedAt(LocalDateTime.now());

                    p.getFamilyMembers().add(fam);
                }
            }
        }

        existing.values().forEach(p.getFamilyMembers()::remove);

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


}
