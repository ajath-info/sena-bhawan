package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.CreatePersonnelRequest;
import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.entity.formation.Command;
import com.example.sena_bhawan.entity.formation.Corps;
import com.example.sena_bhawan.entity.formation.Division;
import com.example.sena_bhawan.projection.AgeBandProjection;
import com.example.sena_bhawan.projection.MedicalCategoryProjection;
import com.example.sena_bhawan.projection.PostingDetailsProjection;
import com.example.sena_bhawan.projection.RetirementYearProjection;
import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.service.PersonnelService;
import com.example.sena_bhawan.specification.PersonnelSpecification;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonnelServiceImpl implements PersonnelService {

    private final UnitMasterRepository unitMasterRepository;
    private final PostingDetailsRepository postingDetailsRepository;
    private final PersonnelRepository personnelRepository;
    private final CoursePanelRepository coursePanelRepository;
    private final String IMAGE_UPLOAD_DIR = "uploads/officer-images/";
    private final ObjectMapper objectMapper;


    @Override
    public List<PersonnelDTO> searchPersonnels(String term) {
        // Step 1: Validate search term
        validateSearchTerm(term);

        log.info("Searching Personnel with term: {}", term);

        // Step 2: Search in database
        List<Personnel> personnels = personnelRepository.findDistinctByArmyNoStartingWith(term);

        // Step 3: Convert to DTO (only id and name)
        return personnels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public void validateSearchTerm(String term) {
        if (term == null || term.trim().length() < 4) {
            throw new RuntimeException("Minimum 4 characters required for search");
        }
    }

    private PersonnelDTO convertToDTO(Personnel personnel) {
        PersonnelDTO dto = new PersonnelDTO();
        dto.setId(personnel.getId());
        dto.setArmyNo(personnel.getArmyNo());
        dto.setFullName(personnel.getFullName());
        dto.setRank(personnel.getRank());
        return dto;
    }

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
    private final OrbatStructureRepository orbatStructureRepository;

    @Override
    public RetirementForecastResponse getRetirementForecast() {
        int currentYear = LocalDate.now().getYear();
        int endYear = currentYear + 4;  // 5 years forecast

        // Using Projection instead of Object[]
        List<RetirementYearProjection> projections =
                personnelRepository.getRetirementForecast(currentYear, endYear);

        // Create map of year -> count
        Map<Integer, Long> forecastMap = new HashMap<>();
        for (RetirementYearProjection projection : projections) {
            forecastMap.put(projection.getRetirementYear(), projection.getCount());
        }

        // Prepare labels and data in correct order
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (int year = currentYear; year <= endYear; year++) {
            labels.add(String.valueOf(year));
            data.add(forecastMap.getOrDefault(year, 0L).intValue());
        }

        return RetirementForecastResponse.builder()
                .labels(labels)
                .data(data)
                .chartType("line")
                .title("Retirement Forecast")
                .build();
    }

//    @Override
//    public MedicalCategoryResponse getMedicalCategoryDistribution() {
//        List<MedicalCategoryProjection> projections =
//                personnelRepository.getMedicalCategoryCounts();
//
//        List<String> labels = new ArrayList<>();
//        List<Integer> data = new ArrayList<>();
//
//        for (MedicalCategoryProjection projection : projections) {
//            String category = projection.getMedicalCategory();
//            if (category != null && !category.trim().isEmpty()) {
//                labels.add(category.trim());
//                data.add(projection.getCount().intValue());
//            }
//        }
//
//        return MedicalCategoryResponse.builder()
//                .labels(labels)
//                .data(data)
//                .chartType("doughnut")
//                .title("Medical Category Distribution")
//                .build();
//    }

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

//    public PersonnelServiceImpl(PersonnelRepository personnelRepository, UnitMasterRepository unitMasterRepository, PostingDetailsRepository postingDetailsRepository, CoursePanelRepository coursePanelRepository, ObjectMapper objectMapper, OrbatStructureRepository orbatStructureRepository) {
//        this.personnelRepository = personnelRepository;
//        this.unitMasterRepository= unitMasterRepository;
//        this.postingDetailsRepository=postingDetailsRepository;
//        this.coursePanelRepository=coursePanelRepository;
//        this.objectMapper = objectMapper;
//        this.orbatStructureRepository = orbatStructureRepository;
//    }

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
        try {
            Personnel p = new Personnel();

            // Basic Info
            p.setCommissionType(req.getCommission());
            p.setArmyNo(req.getArmyNo());
            p.setRank(req.getRank());
            p.setFirstName(req.getFirstName());
            p.setLastName(req.getLastName());
            p.setFullName(req.getFullName());
            p.setGender(req.getGender());
            p.setCaseType(req.getCaseType());
            p.setDateOfCommission(req.getDateOfCommission());
            p.setDateOfSeniority(req.getDateOfSeniority());
            p.setDateOfBirth(req.getDateOfBirth());
            p.setPlaceOfBirth(req.getPlaceOfBirth());

            // Image handling
            if (officerImage != null && !officerImage.isEmpty()) {
                String imagePath = saveOfficerImage(officerImage);
                p.setOfficerImage(imagePath);
            }

            // Service Details
            p.setNrs(req.getNrs());
            p.setNearestAirport(req.getNearestAirport());
            p.setReligion(req.getReligion());
            p.setAadhaarNumber(req.getAadhaarNumber());
            p.setPanCard(req.getPanCard());
            p.setMaritalStatus(req.getMaritalStatus());
            p.setCdaAccountNo(req.getCdaAccountNo());

            // Address
            p.setPermanentAddress(req.getPermanentAddress());
            p.setCity(req.getCity());
            p.setDistrict(req.getDistrict());
            p.setState(req.getState());
            p.setPinCode(req.getPinCode());

            // Contact
            p.setMobileNumber(req.getMobileNumber());
            p.setAlternateMobile(req.getAlternateMobile());
            p.setEmailAddress(req.getEmailAddress());
            p.setNicEmail(req.getNicEmail());

            // Medical
            if (req.getMedical() != null) {
                p.setMedicalCode(req.getMedical().getMedicalCode());

                // Set medical values
                if (req.getMedical().getMedicalValues() != null) {
                    p.setMedicalValuesS(req.getMedical().getMedicalValues().getS());
                    p.setMedicalValuesH(req.getMedical().getMedicalValues().getH());
                    p.setMedicalValuesA(req.getMedical().getMedicalValues().getA());
                    p.setMedicalValuesP(req.getMedical().getMedicalValues().getP());
                    p.setMedicalValuesE(req.getMedical().getMedicalValues().getE());
                }
            }

            // Handle Decorations
            if (req.getDecorations() != null) {
                p.setDecorations(req.getDecorations().stream().map(d -> {
                    PersonnelDecorations deco = new PersonnelDecorations();
                    deco.setDecorationCategory(d.getDecorationCategory());
                    deco.setDecorationName(d.getDecorationName());
                    deco.setPersonnel(p);
                    deco.setCreatedAt(LocalDateTime.now());
                    return deco;
                }).collect(Collectors.toList()));
            }

            // Handle Qualifications
            if (req.getQualifications() != null) {
                p.setQualifications(req.getQualifications().stream().map(q -> {
                    PersonnelQualifications pq = new PersonnelQualifications();
                    pq.setQualification(q.getQualification());
                    pq.setBoard(q.getBoard());
                    pq.setInstitution(q.getInstitution());
                    pq.setYearOfCompletion(q.getYearOfCompletion());
                    pq.setGradePercentage(q.getGradePercentage());
                    pq.setPart2OrderNo(q.getPart2OrderNo());
                    pq.setPersonnel(p);
                    pq.setCreatedAt(LocalDateTime.now());
                    return pq;
                }).collect(Collectors.toList()));
            }

            // Handle Additional Qualifications
            if (req.getAdditionalQualifications() != null) {
                p.setAdditionalQualifications(req.getAdditionalQualifications().stream().map(a -> {
                    PersonnelAdditionalQualifications aq = new PersonnelAdditionalQualifications();
                    aq.setQualification(a.getQualification());
                    aq.setAuthorityNo(a.getAuthorityNo());
                    aq.setDate(a.getDate());
                    aq.setLocation(a.getLocation());
                    aq.setPart2OrderNo(a.getPart2OrderNo());
                    aq.setPersonnel(p);
                    aq.setCreatedAt(LocalDate.now());
                    return aq;
                }).collect(Collectors.toList()));
            }

            // Handle Sports
            if (req.getSports() != null) {
                p.setSports(req.getSports().stream().map(s -> {
                    PersonnelSports ps = new PersonnelSports();
                    ps.setSportName(s.getSportName());
                    ps.setLevel(s.getSportsLevel());
                    ps.setPlace(s.getPlace());
                    ps.setRemarks(s.getAchievements());
                    ps.setPersonnel(p);
                    ps.setCreatedAt(LocalDate.now());
                    return ps;
                }).collect(Collectors.toList()));
            }

            // Handle Family
            if (req.getFamily() != null) {
                p.setFamilyMembers(req.getFamily().stream().map(f -> {
                    PersonnelFamily fam = new PersonnelFamily();
                    fam.setFirstName(f.getFirstName());
                    fam.setLastName(f.getLastName());
                    fam.setName(f.getFullName());
                    fam.setRelationship(f.getRelationship());
                    fam.setContactNumber(f.getContactNumber());
                    fam.setPart2OrderNo(f.getPart2OrderNo());
                    fam.setOrderDate(f.getDate());
                    fam.setPersonnel(p);
                    fam.setCreatedAt(LocalDateTime.now());
                    return fam;
                }).collect(Collectors.toList()));
            }

            // Handle Medical Details
            if (req.getMedical() != null && req.getMedical().getMedicalDetails() != null) {
                p.setMedicalDetails(req.getMedical().getMedicalDetails().stream().map(m -> {
                    PersonnelMedicalDetails md = new PersonnelMedicalDetails();
                    md.setMedicalCategory(m.getCategory());
                    md.setMedicalValue(m.getValue());
                    md.setType(m.getType());
                    md.setPeriod(m.getPeriod());
                    md.setRemark(m.getRemark());
                    md.setPersonnel(p);
                    md.setCreatedAt(LocalDateTime.now());
                    return md;
                }).collect(Collectors.toList()));
            }

            Personnel saved = personnelRepository.save(p);
            log.info("Personnel saved successfully with ID: {}", saved.getId());
            return saved.getId();

        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate data error: {}", e.getMessage());
            throw new RuntimeException("Duplicate data error: Army No, Aadhaar, PAN, or Email already exists");
        } catch (Exception e) {
            log.error("Error saving personnel: {}", e.getMessage(), e);
            throw new RuntimeException("Something went wrong while saving personnel: " + e.getMessage());
        }
    }

    private String saveOfficerImage(MultipartFile file) {
        try {
            // Create directory if not exists
            Path uploadPath = Paths.get(IMAGE_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();
        } catch (IOException e) {
            log.error("Failed to save officer image: {}", e.getMessage());
            throw new RuntimeException("Failed to save officer image");
        }
    }

//    private String generateMedicalCode(CreatePersonnelRequest.MedicalDTO medical) {
//        if (medical == null || medical.medicalValues == null) {
//            return null;
//        }
//
//        StringBuilder code = new StringBuilder();
//
//        // Always include all categories in order: S, H, A, P, E
//        code.append("S").append(medical.medicalValues.S != null ? medical.medicalValues.S : "1");
//        code.append("H").append(medical.medicalValues.H != null ? medical.medicalValues.H : "1");
//        code.append("A").append(medical.medicalValues.A != null ? medical.medicalValues.A : "1");
//        code.append("P").append(medical.medicalValues.P != null ? medical.medicalValues.P : "1");
//        code.append("E").append(medical.medicalValues.E != null ? medical.medicalValues.E : "1");
//
//        return code.toString();
//    }

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

//    private String saveOfficerImage(MultipartFile file) {
//
//        try {
//            String folder = "uploads/personnel/officer/";
//
//            File dir = new File(folder);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//
//            String fileName =
//                    System.currentTimeMillis() + "_" + file.getOriginalFilename();
//
//            Path path = Paths.get(folder + fileName);
//            Files.write(path, file.getBytes());
//
//            // return RELATIVE path (stored in DB)
//            return folder + fileName;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to save officer image", e);
//        }
//    }

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
                q.setInstitution(r.institution);
                q.setYearOfCompletion(r.yearOfCompletion);
                q.setGradePercentage(r.gradePercentage);

            } else {
                // ADD NEW
                PersonnelQualifications q = new PersonnelQualifications();
                q.setQualification(r.qualification);
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
                aq.setAuthorityNo(r.issuingAuthority);
//                aq.setYear(r.year);
//                aq.setValidity(r.validity);

            } else {
                // ADD NEW
                PersonnelAdditionalQualifications aq =
                        new PersonnelAdditionalQualifications();

                aq.setQualification(r.qualification);
                aq.setAuthorityNo(r.issuingAuthority);
//                aq.setYear(r.year);
//                aq.setValidity(r.validity);
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

//        p.setMedicalCategory(req.medicalCategory);
//        p.setMedicalDate(req.medicalDate);
//        p.setDiagnosis(req.diagnosis);
//        p.setReviewDate(req.reviewDate);
//        p.setRestriction(req.restriction);
//        p.setInjuryCategory(req.injuryCategory);

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
//        p.setNsgEmail(req.nsgEmail);
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


    @Transactional(readOnly = true)
    public Page<PersonnelListDTO> filterPersonnel(PersonnelFilterRequest filter) {
        try {
            // Set defaults
            if (filter.page < 0) filter.page = 0;
            if (filter.size <= 0) filter.size = 1000;

            // Build specification for filters
            Specification<Personnel> spec = buildSpecification(filter);

            // Get paginated personnel IDs using Specification
            Pageable pageable = PageRequest.of(filter.page, filter.size, Sort.by(Sort.Direction.DESC, "id"));
            Page<Personnel> personnelPage = personnelRepository.findAll(spec, pageable);

            // Get IDs
            List<Long> ids = personnelPage.getContent().stream()
                    .map(Personnel::getId)
                    .collect(Collectors.toList());

            // Fetch detailed data with native query
            List<PersonnelListDTO> dtos = new ArrayList<>();
            if (!ids.isEmpty()) {
                List<Object[]> results = personnelRepository.findPersonnelWithDetailsByIds(ids);
                dtos = results.stream()
                        .map(this::mapToPersonnelListDTO)
                        .collect(Collectors.toList());
            }

            return new PageImpl<>(dtos, pageable, personnelPage.getTotalElements());

        } catch (Exception e) {
            log.error("Error filtering personnel: {}", e.getMessage(), e);
            throw new RuntimeException("Error filtering personnel: " + e.getMessage());
        }
    }

    private Specification<Personnel> buildSpecification(PersonnelFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Ensure we're working with distinct results for joins
            if (hasAnyJoinFilter(filter)) {
                query.distinct(true);
            }

            // Army No / Search
            if (filter.search != null && !filter.search.isEmpty()) {
                String searchPattern = "%" + filter.search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("armyNo")), searchPattern),
                        cb.like(cb.lower(root.get("fullName")), searchPattern),
                        cb.like(cb.lower(root.get("rank")), searchPattern)
                ));
            }

            // Place of Birth
            if (filter.placeOfBirth != null && !filter.placeOfBirth.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("placeOfBirth")),
                        "%" + filter.placeOfBirth.toLowerCase() + "%"));
            }

            // Date of Birth
            if (filter.dobGreaterThan != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfBirth"), filter.dobGreaterThan));
            }

            // Date of Commission
            if (filter.docFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfCommission"), filter.docFrom));
            }
            if (filter.docTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateOfCommission"), filter.docTo));
            }

            // Date of Seniority
            if (filter.dosFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfSeniority"), filter.dosFrom));
            }
            if (filter.dosTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateOfSeniority"), filter.dosTo));
            }

            // Rank IN clause
            if (filter.rank != null && !filter.rank.isEmpty()) {
                predicates.add(root.get("rank").in(filter.rank));
            }

            // Medical Category - split S, H, A, P, E values
            if (filter.medicalCategory != null && !filter.medicalCategory.isEmpty()) {
                predicates.add(buildMedicalCategoryPredicate(root, query, cb, filter.medicalCategory));
            }

            // Command filter - through posting details
            if (filter.command != null && !filter.command.isEmpty()) {
                predicates.add(buildCommandPredicate(root, query, cb, filter.command));
            }

            // Corps filter - through posting details
            if (filter.corps != null && !filter.corps.isEmpty()) {
                predicates.add(buildCorpsPredicate(root, query, cb, filter.corps));
            }

            // Division filter - through posting details
            if (filter.division != null && !filter.division.isEmpty()) {
                predicates.add(buildDivisionPredicate(root, query, cb, filter.division));
            }

            // Establishment Type filter - through posting details
            if (filter.establishmentType != null && !filter.establishmentType.isEmpty()) {
                predicates.add(buildEstablishmentTypePredicate(root, query, cb, filter.establishmentType));
            }

            // Area Type filter - through orbat structure
            if (filter.areaType != null && !filter.areaType.isEmpty()) {
                predicates.add(buildAreaTypePredicate(root, query, cb, filter.areaType));
            }

            // Civil Qualifications - through personnel_qualifications
            if (filter.civilQualification != null && !filter.civilQualification.isEmpty()) {
                predicates.add(buildCivilQualificationPredicate(root, query, cb, filter.civilQualification));
            }

            // Sports - through personnel_sports
            if (filter.sports != null && !filter.sports.isEmpty()) {
                predicates.add(buildSportsPredicate(root, query, cb, filter.sports));
            }

            // Course Name - through course_panel_nomination
            if (filter.courseName != null && !filter.courseName.isEmpty()) {
                predicates.add(buildCourseNamePredicate(root, query, cb, filter.courseName));
            }

            // Posting Due Months - based on tenure end dates
            if (filter.postingDueMonths != null && !filter.postingDueMonths.isEmpty()) {
                predicates.add(buildPostingDueMonthsPredicate(root, query, cb, filter.postingDueMonths));
            }

            // TOS (Tenure of Service) range
            if (filter.tosFrom != null || filter.tosTo != null) {
                predicates.add(buildTOSPredicate(root, query, cb, filter.tosFrom, filter.tosTo));
            }

            // Course from/to dates
            if (filter.courseFrom != null || filter.courseTo != null) {
                predicates.add(buildCourseDatePredicate(root, query, cb, filter.courseFrom, filter.courseTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private boolean hasAnyJoinFilter(PersonnelFilterRequest filter) {
        return filter.command != null || filter.corps != null || filter.division != null ||
                filter.establishmentType != null || filter.areaType != null ||
                filter.civilQualification != null || filter.sports != null ||
                filter.courseName != null || filter.postingDueMonths != null ||
                filter.tosFrom != null || filter.tosTo != null ||
                filter.courseFrom != null || filter.courseTo != null ||
                (filter.medicalCategory != null && !filter.medicalCategory.isEmpty());
    }

    private Predicate buildMedicalCategoryPredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                                    CriteriaBuilder cb, List<String> medicalCategories) {
        // Assuming medicalCode is stored as format like "S3H2A1P1E1"
        List<Predicate> medicalPredicates = new ArrayList<>();

        for (String category : medicalCategories) {
            // Parse the medical category code (e.g., "S3H2A1P1E1")
            if (category.length() >= 10) {
                medicalPredicates.add(cb.like(root.get("medicalCode"), category));
            }
        }

        return cb.or(medicalPredicates.toArray(new Predicate[0]));
    }

    private Predicate buildCommandPredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                            CriteriaBuilder cb, List<String> commands) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<PostingDetails> postingRoot = subquery.from(PostingDetails.class);
        Join<PostingDetails, OrbatStructure> orbatJoin = postingRoot.join("orbatStructure");
        Join<OrbatStructure, Corps> corpsJoin = orbatJoin.join("corps");
        Join<Corps, Command> commandJoin = corpsJoin.join("command");

        subquery.select(postingRoot.get("personnel").get("id"))
                .where(cb.equal(postingRoot.get("personnel").get("id"), root.get("id")),
                        cb.lower(commandJoin.get("commandName")).in(
                                commands.stream().map(String::toLowerCase).collect(Collectors.toList())));

        return cb.exists(subquery);
    }

    private Predicate buildCorpsPredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                          CriteriaBuilder cb, List<String> corps) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<PostingDetails> postingRoot = subquery.from(PostingDetails.class);
        Join<PostingDetails, OrbatStructure> orbatJoin = postingRoot.join("orbatStructure");
        Join<OrbatStructure, Corps> corpsJoin = orbatJoin.join("corps");

        subquery.select(postingRoot.get("personnel").get("id"))
                .where(cb.equal(postingRoot.get("personnel").get("id"), root.get("id")),
                        cb.lower(corpsJoin.get("corpsName")).in(
                                corps.stream().map(String::toLowerCase).collect(Collectors.toList())));

        return cb.exists(subquery);
    }

    private Predicate buildDivisionPredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                             CriteriaBuilder cb, List<String> divisions) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<PostingDetails> postingRoot = subquery.from(PostingDetails.class);
        Join<PostingDetails, OrbatStructure> orbatJoin = postingRoot.join("orbatStructure");
        Join<OrbatStructure, Division> divisionJoin = orbatJoin.join("division");

        subquery.select(postingRoot.get("personnel").get("id"))
                .where(cb.equal(postingRoot.get("personnel").get("id"), root.get("id")),
                        cb.lower(divisionJoin.get("divisionName")).in(
                                divisions.stream().map(String::toLowerCase).collect(Collectors.toList())));

        return cb.exists(subquery);
    }

    private Predicate buildEstablishmentTypePredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                                      CriteriaBuilder cb, List<String> establishmentTypes) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<PostingDetails> postingRoot = subquery.from(PostingDetails.class);
        Join<PostingDetails, FormationEstablishment> formationJoin = postingRoot.join("formationEstablishment");

        subquery.select(postingRoot.get("personnel").get("id"))
                .where(cb.equal(postingRoot.get("personnel").get("id"), root.get("id")),
                        cb.lower(formationJoin.get("establishmentType")).in(
                                establishmentTypes.stream().map(String::toLowerCase).collect(Collectors.toList())));

        return cb.exists(subquery);
    }

    private Predicate buildAreaTypePredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                             CriteriaBuilder cb, List<String> areaTypes) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<PostingDetails> postingRoot = subquery.from(PostingDetails.class);
        Join<PostingDetails, OrbatStructure> orbatJoin = postingRoot.join("orbatStructure");

        subquery.select(postingRoot.get("personnel").get("id"))
                .where(cb.equal(postingRoot.get("personnel").get("id"), root.get("id")),
                        cb.lower(orbatJoin.get("areaType")).in(
                                areaTypes.stream().map(String::toLowerCase).collect(Collectors.toList())));

        return cb.exists(subquery);
    }

    private Predicate buildCivilQualificationPredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                                       CriteriaBuilder cb, List<String> qualifications) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<PersonnelQualifications> qualRoot = subquery.from(PersonnelQualifications.class);

        subquery.select(qualRoot.get("personnel").get("id"))
                .where(cb.equal(qualRoot.get("personnel").get("id"), root.get("id")),
                        cb.lower(qualRoot.get("qualification")).in(
                                qualifications.stream().map(String::toLowerCase).collect(Collectors.toList())));

        return cb.exists(subquery);
    }

    private Predicate buildSportsPredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                           CriteriaBuilder cb, List<String> sports) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<PersonnelSports> sportsRoot = subquery.from(PersonnelSports.class);

        subquery.select(sportsRoot.get("personnel").get("id"))
                .where(cb.equal(sportsRoot.get("personnel").get("id"), root.get("id")),
                        cb.lower(sportsRoot.get("sportName")).in(
                                sports.stream().map(String::toLowerCase).collect(Collectors.toList())));

        return cb.exists(subquery);
    }

    private Predicate buildCourseNamePredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                               CriteriaBuilder cb, String courseName) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<CoursePanelNomination> nominationRoot = subquery.from(CoursePanelNomination.class);
        Join<CoursePanelNomination, CourseSchedule> scheduleJoin = nominationRoot.join("schedule");
        Join<CourseSchedule, CourseMaster> courseJoin = scheduleJoin.join("course");

        subquery.select(nominationRoot.get("personnel").get("id"))
                .where(cb.equal(nominationRoot.get("personnel").get("id"), root.get("id")),
                        cb.like(cb.lower(courseJoin.get("courseName")),
                                "%" + courseName.toLowerCase() + "%"));

        return cb.exists(subquery);
    }

    private Predicate buildPostingDueMonthsPredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                                     CriteriaBuilder cb, List<Integer> dueMonths) {
        List<Predicate> monthPredicates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (Integer months : dueMonths) {
            LocalDate dueDate = currentDate.plusMonths(months);
            // Assuming you have a tenure_end_date in posting_details
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<PostingDetails> postingRoot = subquery.from(PostingDetails.class);

            subquery.select(postingRoot.get("personnel").get("id"))
                    .where(cb.equal(postingRoot.get("personnel").get("id"), root.get("id")),
                            cb.lessThanOrEqualTo(postingRoot.get("tenureEndDate"), dueDate),
                            cb.greaterThanOrEqualTo(postingRoot.get("tenureEndDate"), currentDate));

            monthPredicates.add(cb.exists(subquery));
        }

        return cb.or(monthPredicates.toArray(new Predicate[0]));
    }

    private Predicate buildTOSPredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                        CriteriaBuilder cb, LocalDate tosFrom, LocalDate tosTo) {
        // TOS (Tenure of Service) - calculate from date of commission
        List<Predicate> tosPredicates = new ArrayList<>();

        if (tosFrom != null) {
            tosPredicates.add(cb.greaterThanOrEqualTo(root.get("dateOfCommission"), tosFrom));
        }
        if (tosTo != null) {
            tosPredicates.add(cb.lessThanOrEqualTo(root.get("dateOfCommission"), tosTo));
        }

        return cb.and(tosPredicates.toArray(new Predicate[0]));
    }

    private Predicate buildCourseDatePredicate(Root<Personnel> root, CriteriaQuery<?> query,
                                               CriteriaBuilder cb, LocalDate courseFrom, LocalDate courseTo) {
        Subquery <Long> subquery = query.subquery(Long.class);
        Root<CoursePanelNomination> nominationRoot = subquery.from(CoursePanelNomination.class);
        Join<CoursePanelNomination, CourseSchedule> scheduleJoin = nominationRoot.join("schedule");

        List<Predicate> datePredicates = new ArrayList<>();

        if (courseFrom != null) {
            datePredicates.add(cb.greaterThanOrEqualTo(scheduleJoin.get("startDate"), courseFrom));
        }
        if (courseTo != null) {
            datePredicates.add(cb.lessThanOrEqualTo(scheduleJoin.get("endDate"), courseTo));
        }

        subquery.select(nominationRoot.get("personnel").get("id"))
                .where(cb.equal(nominationRoot.get("personnel").get("id"), root.get("id")),
                        cb.and(datePredicates.toArray(new Predicate[0])));

        return cb.exists(subquery);
    }

    private PersonnelListDTO mapToPersonnelListDTO(Object[] row) {
        PersonnelListDTO dto = new PersonnelListDTO();

        int idx = 0;
        dto.setId(asLong(row[idx++]));
        dto.setArmyNo(asString(row[idx++]));
        dto.setRank(asString(row[idx++]));
        dto.setFullName(asString(row[idx++]));
        dto.setDateOfBirth(asString(row[idx++]));
        dto.setDateOfCommission(asString(row[idx++]));
        dto.setDateOfSeniority(asString(row[idx++]));
        dto.setMedicalCode(asString(row[idx++]));
        dto.setReligion(asString(row[idx++]));
        dto.setMaritalStatus(asString(row[idx++]));
        dto.setMobileNumber(asString(row[idx++]));
        dto.setEmailAddress(asString(row[idx++]));
        dto.setCity(asString(row[idx++]));
        dto.setState(asString(row[idx++]));
        dto.setPlaceOfBirth(asString(row[idx++]));

        // Unit (JSONB)
        Object unitObj = row[idx++];
        if (unitObj != null) {
            try {
                String unitJson = unitObj.toString();
                JsonNode node = objectMapper.readTree(unitJson);
                dto.setUnit(node.get("unit_name").asText());
                dto.setAreaType(node.get("area_type").asText());
            } catch (Exception e) {
                dto.setUnit("-");
                dto.setAreaType("-");
            }
        } else {
            dto.setUnit("-");
            dto.setAreaType("-");
        }

        dto.setDivision(asString(row[idx++]));
        dto.setEstablishmentType(asString(row[idx++]));
        dto.setCommand(asString(row[idx++]));
        dto.setCorps(asString(row[idx++]));
        dto.setCourse(asString(row[idx++]));
        dto.setCivilQual(asString(row[idx++]));
        dto.setSports(asString(row[idx++]));

        return dto;
    }

    private String asString(Object obj) {
        return obj != null ? obj.toString() : "-";
    }

    private Long asLong(Object obj) {
        return obj != null ? Long.parseLong(obj.toString()) : null;
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
