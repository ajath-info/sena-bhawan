package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.ParamountDTO.*;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.entity.formation.Command;
import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.repository.formation.CommandRepository;
import com.example.sena_bhawan.repository.paramount.PersonnelDecorationsRepository;
import com.example.sena_bhawan.repository.paramount.PersonnelFamilyRepository;
import com.example.sena_bhawan.repository.paramount.PersonnelMedicalDetailsRepository;
import com.example.sena_bhawan.repository.paramount.PersonnelQualificationsRepository;
import com.example.sena_bhawan.service.PersonnelProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelProfileServiceImpl implements PersonnelProfileService {

    private final PersonnelRepository personnelRepository;
    private final PostingDetailsRepository postingDetailsRepository;
    private final OrbatStructureRepository orbatStructureRepository;
    private final CommandRepository commandRepository;
    private final CoursePanelRepository coursePanelNominationRepository;
    private final CourseScheduleRepository courseScheduleRepository;
    private final CourseMasterRepository courseMasterRepository;
    private final PersonnelDecorationsRepository personnelDecorationsRepository;
    private final PersonnelQualificationsRepository personnelQualificationsRepository;
    private final PersonnelAdditionalQualificationsRepository personnelAdditionalQualificationsRepository;
    private final PersonnelFamilyRepository personnelFamilyRepository;
    private final PersonnelMedicalDetailsRepository personnelMedicalDetailsRepository;

    @Override
    public IdentityAndServiceDto getIdentityAndService(Long personnelId) {
        try {
            Optional<Personnel> personnelOpt = personnelRepository.findById(personnelId);
            if (personnelOpt.isEmpty()) {
                log.warn("Personnel not found with ID: {}", personnelId);
                return null;
            }

            Personnel p = personnelOpt.get();

            // Get decorations to compute initials
            List<DecorationDto> decorations = getDecorations(personnelId);
            String decorationInitials = getDecorationInitials(decorations);

            return IdentityAndServiceDto.builder()
                    .armyNo(p.getArmyNo())
                    .rank(p.getRank())
                    .fullName(p.getFullName())
                    .gender(p.getGender())
                    .dateOfBirth(p.getDateOfBirth())
                    .dateOfCommission(p.getDateOfCommission())
                    .dateOfSeniority(p.getDateOfSeniority())
                    .panCard(p.getPanCard())
                    .aadhaarNumber(p.getAadhaarNumber())
                    .lastRank(p.getRank())
                    .mobileNumber(p.getMobileNumber())
                    .officerImage(p.getOfficerImage())
                    .decorationInitials(decorationInitials)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching identity and service for personnel {}: {}", personnelId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<PostingHistoryDto> getPostingHistory(Long personnelId) {
        try {
            List<PostingDetails> postings = postingDetailsRepository
                    .findByPersonnelIdOrderByFromDateDesc(personnelId);

            if (CollectionUtils.isEmpty(postings)) {
                log.info("No posting records found for personnel: {}", personnelId);
                return Collections.emptyList();
            }

            // Step 1: Collect all orbat IDs from postings
            Set<Long> orbatIds = postings.stream()
                    .map(PostingDetails::getOrbatId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Step 2: Fetch all OrbatStructure records in one query
            Map<Long, OrbatStructure> orbatMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(orbatIds)) {
                List<OrbatStructure> orbats = orbatStructureRepository.findAllById(orbatIds);
                if (!CollectionUtils.isEmpty(orbats)) {
                    orbatMap = orbats.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toMap(
                                    OrbatStructure::getId,
                                    o -> o,
                                    (o1, o2) -> o1
                            ));
                }
            }

            // Step 3: Collect all command codes from OrbatStructure
            Set<String> commandCodes = orbatMap.values().stream()
                    .map(OrbatStructure::getCommandCode)
                    .filter(code -> code != null && !code.trim().isEmpty())
                    .collect(Collectors.toSet());

            // Step 4: Fetch all Command records in one query
            Map<String, Command> commandMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(commandCodes)) {
                List<Command> commands = commandRepository.findByCommandCodeIn(commandCodes);
                if (!CollectionUtils.isEmpty(commands)) {
                    commandMap = commands.stream()
                            .filter(Objects::nonNull)
                            .filter(cmd -> cmd.getCommandCode() != null)
                            .collect(Collectors.toMap(
                                    Command::getCommandCode,
                                    cmd -> cmd,
                                    (cmd1, cmd2) -> cmd1
                            ));
                }
            }

            // Step 5: Build DTOs with proper null handling
            List<PostingHistoryDto> result = new ArrayList<>();

            for (PostingDetails posting : postings) {
                try {
                    PostingHistoryDto.PostingHistoryDtoBuilder builder = PostingHistoryDto.builder()
                            .postingId(posting.getPostingId())
                            .unitName(posting.getUnitName() != null ? posting.getUnitName() : "N/A")
                            .appointment(posting.getAppointment() != null ? posting.getAppointment() : "N/A")
                            .duration(posting.getDuration() != null ? posting.getDuration() : "N/A")
                            .takenOnStrength(posting.getFromDate());

                    // Default values
                    String commandName = "N/A";
                    String typeOfUnit = "N/A";

                    // Get command name using ORBAT mapping
                    if (posting.getOrbatId() != null) {
                        OrbatStructure orbat = orbatMap.get(posting.getOrbatId());
                        if (orbat != null) {
                            // Get type of unit from ORBAT
                            if (orbat.getUnitType() != null && !orbat.getUnitType().trim().isEmpty()) {
                                typeOfUnit = orbat.getUnitType();
                            }

                            // Get command name using command code
                            String commandCode = orbat.getCommandCode();
                            if (commandCode != null && !commandCode.trim().isEmpty()) {
                                Command command = commandMap.get(commandCode);
                                if (command != null && command.getCommandName() != null
                                        && !command.getCommandName().trim().isEmpty()) {
                                    commandName = command.getCommandName();
                                }
                            }
                        }
                    }

                    builder.command(commandName);
                    builder.typeOfUnit(typeOfUnit);
                    result.add(builder.build());

                } catch (Exception e) {
                    log.error("Error processing posting record with ID: {} for personnel: {}",
                            posting.getPostingId(), personnelId, e);
                }
            }

            log.info("Found {} posting records for personnel: {}", result.size(), personnelId);
            return result;

        } catch (Exception e) {
            log.error("Error fetching posting history for personnel {}: {}", personnelId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<CourseCompletedDto> getCoursesCompleted(Long personnelId) {
        try {
            // Step 1: Get all nominations for the personnel from CoursePanelNomination table
            List<CoursePanelNomination> nominations = coursePanelNominationRepository
                    .findByPersonnelIdIn(Collections.singletonList(personnelId));

            if (CollectionUtils.isEmpty(nominations)) {
                log.info("No nominations found for personnel: {}", personnelId);
                return Collections.emptyList();
            }

            // Step 2: Extract all schedule IDs from nominations
            Set<Long> scheduleIds = nominations.stream()
                    .map(CoursePanelNomination::getScheduleId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(scheduleIds)) {
                log.info("No schedule IDs found for personnel: {}", personnelId);
                return Collections.emptyList();
            }

            // Step 3: Fetch all course schedules using schedule IDs
            List<CourseSchedule> schedules = courseScheduleRepository.findByScheduleIdIn(scheduleIds);

            // Create map of scheduleId -> CourseSchedule for quick lookup
            Map<Long, CourseSchedule> scheduleMap = schedules.stream()
                    .filter(s -> s != null && s.getScheduleId() != null)
                    .collect(Collectors.toMap(
                            CourseSchedule::getScheduleId,
                            s -> s,
                            (s1, s2) -> s1
                    ));

            // Step 4: Extract all course IDs from schedules
            Set<Integer> courseIds = schedules.stream()
                    .map(s -> s.getCourse() != null ? s.getCourse().getSrno() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Step 5: Fetch all course masters using course IDs
            Map<Integer, CourseMaster> courseMasterMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(courseIds)) {
                List<CourseMaster> courseMasters = courseMasterRepository.findBySrnoIn(courseIds);
                courseMasterMap = courseMasters.stream()
                        .filter(cm -> cm != null && cm.getSrno() != null)
                        .collect(Collectors.toMap(
                                CourseMaster::getSrno,
                                cm -> cm,
                                (cm1, cm2) -> cm1
                        ));
            }

            // Step 6: Build DTOs
            List<CourseCompletedDto> result = new ArrayList<>();

            for (CoursePanelNomination nomination : nominations) {
                Long scheduleId = nomination.getScheduleId();
                CourseSchedule schedule = scheduleMap.get(scheduleId);

                if (schedule == null) {
                    log.warn("Schedule not found for scheduleId: {}", scheduleId);
                    continue;
                }

                // Get course name from CourseMaster and append batch number
                String courseName = null;
                if (schedule.getCourse() != null && schedule.getCourse().getSrno() != null) {
                    CourseMaster courseMaster = courseMasterMap.get(schedule.getCourse().getSrno());
                    if (courseMaster != null) {
                        courseName = courseMaster.getCourseName();
                        // Append batch number if available
                        if (schedule.getBatchNumber() != null) {
                            courseName = courseName + " - " + schedule.getBatchNumber();
                        }
                    }
                }

                // Handle grading - if null, set to "Pending"
                String grading = nomination.getGrade();
                if (grading == null) {
                    grading = "Pending";
                }

                // Build DTO
                CourseCompletedDto dto = CourseCompletedDto.builder()
                        .nominationId(nomination.getId())
                        .courseName(courseName)
                        .grading(grading)
                        .fromDate(schedule.getStartDate())
                        .toDate(schedule.getEndDate())
                        .build();

                result.add(dto);
            }

            // Sort by fromDate descending (most recent first)
            result.sort((a, b) -> {
                if (a.getFromDate() == null && b.getFromDate() == null) return 0;
                if (a.getFromDate() == null) return 1;
                if (b.getFromDate() == null) return -1;
                return b.getFromDate().compareTo(a.getFromDate());
            });

            log.info("Found {} completed courses for personnel: {}", result.size(), personnelId);
            return result;

        } catch (Exception e) {
            log.error("Error fetching courses completed for personnel {}: {}",
                    personnelId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<DecorationDto> getDecorations(Long personnelId) {
        try {
            List<PersonnelDecorations> decorations = personnelDecorationsRepository
                    .findByPersonnelIdOrderByAwardDateDesc(personnelId);

            if (CollectionUtils.isEmpty(decorations)) {
                return Collections.emptyList();
            }

            return decorations.stream()
                    .map(d -> {
                        // Generate initials from decoration name
                        String initials = generateDecorationInitials(d.getDecorationName());

                        return DecorationDto.builder()
                                .decorationId(d.getId())
                                .awardName(d.getDecorationName())
                                .awardDate(d.getAwardDate())
                                .citation(d.getCitation())
                                .initials(initials)
                                .build();
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching decorations for personnel {}: {}", personnelId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<QualificationDto> getQualifications(Long personnelId) {
        try {
            List<PersonnelQualifications> qualifications = personnelQualificationsRepository
                    .findByPersonnelId(personnelId);

            if (CollectionUtils.isEmpty(qualifications)) {
                return Collections.emptyList();
            }

            return qualifications.stream()
                    .map(q -> QualificationDto.builder()
                            .qualificationId(q.getId())
                            .qualification(q.getQualification())
                            .institution(q.getInstitution())
                            .year(q.getYearOfCompletion())
                            .grade(q.getGradePercentage())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching qualifications for personnel {}: {}", personnelId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<AdditionalQualificationDto> getAdditionalQualifications(Long personnelId) {
        try {
            List<PersonnelAdditionalQualifications> additionalQualifications =
                    personnelAdditionalQualificationsRepository.findByPersonnelId(personnelId);

            if (CollectionUtils.isEmpty(additionalQualifications)) {
                return Collections.emptyList();
            }

            return additionalQualifications.stream()
                    .map(aq -> AdditionalQualificationDto.builder()
                            .additionalQualificationId(aq.getId())
                            .qualification(aq.getQualification())
                            .issuingAuthority(aq.getAuthorityNo())
                            .year(aq.getYear())
                            .validity(aq.getValidity())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching additional qualifications for personnel {}: {}", personnelId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<FamilyMemberDto> getFamilyMembers(Long personnelId) {
        try {
            List<PersonnelFamily> familyMembers = personnelFamilyRepository
                    .findByPersonnelId(personnelId);

            if (CollectionUtils.isEmpty(familyMembers)) {
                return Collections.emptyList();
            }

            return familyMembers.stream()
                    .map(f -> FamilyMemberDto.builder()
                            .familyMemberId(f.getId())
                            .name(f.getName())
                            .relationship(f.getRelationship())
                            .contact(f.getContactNumber())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching family members for personnel {}: {}", personnelId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public DisciplineAndMedicalDto getDisciplineAndMedical(Long personnelId) {
        try {
            // Get medical details if available
            List<PersonnelMedicalDetails> medicalDetails = personnelMedicalDetailsRepository
                    .findByPersonnelId(personnelId);

            String medicalCategory = null;
            if (!CollectionUtils.isEmpty(medicalDetails)) {
                PersonnelMedicalDetails medical = medicalDetails.get(0);
                medicalCategory = medical.getMedicalCategory();
            }

            // Try to get home station from latest posting
            String homeStation = getHomeStationFromPostings(personnelId);

            return DisciplineAndMedicalDto.builder()
                    .disciplineCase("N/A") // Default as per PDF
                    .restrictions("N/A")
                    .vigilanceStatus("N/A")
                    .medicalCategory(medicalCategory != null ? medicalCategory : "N/A")
                    .schoolInstitute("N/A")
                    .degree("N/A")
                    .course("N/A")
                    .homeStation(homeStation != null ? homeStation : "N/A")
                    .build();

        } catch (Exception e) {
            log.error("Error fetching discipline and medical for personnel {}: {}", personnelId, e.getMessage());
            return DisciplineAndMedicalDto.builder()
                    .disciplineCase("N/A")
                    .restrictions("N/A")
                    .vigilanceStatus("N/A")
                    .medicalCategory("N/A")
                    .schoolInstitute("N/A")
                    .degree("N/A")
                    .course("N/A")
                    .homeStation("N/A")
                    .build();
        }
    }

    /**
     * Helper method to generate decoration initials from award name
     * Example: "Ati Vishisht Seva Medal" -> "AVSM"
     */
    private String generateDecorationInitials(String awardName) {
        if (awardName == null || awardName.trim().isEmpty()) {
            return "";
        }

        // Split by space and take first letter of each word
        String[] words = awardName.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        return initials.toString();
    }

    /**
     * Helper method to get decoration initials as comma-separated string
     */
    private String getDecorationInitials(List<DecorationDto> decorations) {
        if (CollectionUtils.isEmpty(decorations)) {
            return "";
        }

        return decorations.stream()
                .map(DecorationDto::getInitials)
                .filter(initials -> initials != null && !initials.isEmpty())
                .collect(Collectors.joining(", "));
    }

    /**
     * Helper method to get home station from latest posting
     */
    private String getHomeStationFromPostings(Long personnelId) {
        try {
            List<PostingDetails> postings = postingDetailsRepository
                    .findByPersonnelIdOrderByFromDateDesc(personnelId);

            if (CollectionUtils.isEmpty(postings)) {
                return null;
            }

            // Get the latest posting
            PostingDetails latestPosting = postings.get(0);

            // Try to get unit location from ORBAT
            if (latestPosting.getOrbatId() != null) {
                Optional<OrbatStructure> orbatOpt = orbatStructureRepository.findById(latestPosting.getOrbatId());
                if (orbatOpt.isPresent()) {
                    OrbatStructure orbat = orbatOpt.get();
                    if (orbat.getLocation() != null && !orbat.getLocation().trim().isEmpty()) {
                        return orbat.getLocation();
                    }
                }
            }

            // Fallback to unit name if location not found
            return latestPosting.getUnitName();

        } catch (Exception e) {
            log.debug("Error getting home station for personnel {}: {}", personnelId, e.getMessage());
            return null;
        }
    }
}