package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.CourseDetails;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.repository.CourseDetailsRepository;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.repository.PostingDetailsRepository;
import com.example.sena_bhawan.service.OfficerDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfficerDetailsServiceImpl implements OfficerDetailsService {

    private final PostingDetailsRepository postingDetailsRepository;
    private final PersonnelRepository personnelRepository;
    private final CourseDetailsRepository courseDetailsRepository;

    @Override
    public OfficerListResponseDto getOfficersByFormationAndUnit(OfficerListRequestDto requestDto) {
        // Validate input first
        String formationType = requestDto.getFormationType();
        String unitName = requestDto.getUnitName();

        if (formationType == null || formationType.trim().isEmpty() ||
                unitName == null || unitName.trim().isEmpty()) {
            log.error("Invalid input: formationType={}, unitName={}", formationType, unitName);
            return buildEmptyResponse(formationType, unitName);
        }

        log.info("Fetching officers for Formation: {}, Unit: {}", formationType, unitName);

        try {
            // Step 1: Get current personnel IDs
            List<Long> personnelIds = fetchCurrentPersonnelIds(formationType, unitName);
            if (personnelIds.isEmpty()) {
                return buildEmptyResponse(formationType, unitName);
            }

            // Step 2: Fetch personnel details
            List<Personnel> personnelList = fetchPersonnelDetails(personnelIds);
            if (personnelList.isEmpty()) {
                return buildEmptyResponse(formationType, unitName);
            }

            // Step 3: Fetch ALL courses (entire service)
            List<CourseDetails> allCourses = fetchAllCourses(personnelIds);

            // Step 4: Fetch unit-specific courses with posting validation (single optimized query)
            List<CourseDetails> unitCourses = fetchUnitCourses(personnelIds, formationType, unitName);

            // Step 5: Fetch all distinct course IDs ever done in this unit
            Set<Integer> allUnitCourseIds = fetchAllUnitCourseIds(formationType, unitName);

            // Step 6: Create optimized data structures
            Map<Long, List<CourseDetails>> allCoursesMap = createCoursesMap(allCourses);
            Map<Long, List<CourseDetails>> unitCoursesMap = createUnitCoursesMap(unitCourses);

            // Step 7: Calculate summary
            OfficerSummaryResponseDto summary = calculateSummarySection(
                    personnelList, unitCoursesMap, allUnitCourseIds);

            // Step 8: Prepare officer details for table
            List<OfficerDetailResponseDto> officerDetails = prepareOfficerDetails(
                    personnelList, allCoursesMap, unitCoursesMap);

            // Step 9: Log summary
            logSummary(summary, formationType, unitName, officerDetails);

            // Step 10: Build response
            return OfficerListResponseDto.builder()
                    .formationType(formationType)
                    .unitName(unitName)
                    .summary(summary)
                    .officers(officerDetails)
                    .build();

        } catch (Exception e) {
            log.error("Critical error fetching officer details: {}", e.getMessage(), e);
            return buildErrorResponse(formationType, unitName, e.getMessage());
        }
    }

    /**
     * Fetch current personnel IDs with null safety
     */
    private List<Long> fetchCurrentPersonnelIds(String formationType, String unitName) {
        try {
            List<Long> personnelIds = postingDetailsRepository
                    .findPersonnelIdsByFormationTypeAndUnitName(formationType, unitName);

            if (CollectionUtils.isEmpty(personnelIds)) {
                log.info("No personnel found in unit: {}", unitName);
                return Collections.emptyList();
            }

            log.info("Found {} personnel in unit", personnelIds.size());
            return personnelIds;

        } catch (Exception e) {
            log.error("Error fetching personnel IDs: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetch personnel details with null safety
     */
    private List<Personnel> fetchPersonnelDetails(List<Long> personnelIds) {
        try {
            List<Personnel> personnelList = personnelRepository.findByIdIn(personnelIds);

            if (CollectionUtils.isEmpty(personnelList)) {
                log.warn("No personnel details found for IDs: {}", personnelIds);
                return Collections.emptyList();
            }

            return personnelList;

        } catch (Exception e) {
            log.error("Error fetching personnel details: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetch all courses with null safety
     */
    private List<CourseDetails> fetchAllCourses(List<Long> personnelIds) {
        try {
            return courseDetailsRepository.findByPersonnelIdIn(personnelIds);
        } catch (Exception e) {
            log.error("Error fetching courses: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetch unit-specific courses with single optimized query
     */
    private List<CourseDetails> fetchUnitCourses(List<Long> personnelIds,
                                                 String formationType,
                                                 String unitName) {
        try {
            if (CollectionUtils.isEmpty(personnelIds)) {
                return Collections.emptyList();
            }

            return courseDetailsRepository.findUnitCoursesByPersonnelIds(
                    personnelIds, formationType, unitName);

        } catch (Exception e) {
            log.error("Error fetching unit courses: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetch all distinct course IDs ever done in this unit
     */
    private Set<Integer> fetchAllUnitCourseIds(String formationType, String unitName) {
        try {
            Set<Integer> courseIds = courseDetailsRepository
                    .findAllDistinctCourseIdsByUnit(formationType, unitName);

            log.info("Found {} distinct courses ever done in unit {}",
                    courseIds.size(), unitName);
            return courseIds;

        } catch (Exception e) {
            log.error("Error fetching unit course IDs: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * Create map of personnel ID to their courses
     */
    private Map<Long, List<CourseDetails>> createCoursesMap(List<CourseDetails> courses) {
        if (CollectionUtils.isEmpty(courses)) {
            return Collections.emptyMap();
        }

        return courses.stream()
                .filter(c -> c != null && c.getPersonnelId() != null)
                .collect(Collectors.groupingBy(
                        CourseDetails::getPersonnelId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(
                                                CourseDetails::getFromDate,
                                                Comparator.nullsLast(Comparator.naturalOrder())))
                                        .collect(Collectors.toList())
                        )
                ));
    }

    /**
     * Create map of personnel ID to their unit courses
     */
    private Map<Long, List<CourseDetails>> createUnitCoursesMap(List<CourseDetails> unitCourses) {
        if (CollectionUtils.isEmpty(unitCourses)) {
            return Collections.emptyMap();
        }

        return unitCourses.stream()
                .filter(c -> c != null && c.getPersonnelId() != null)
                .collect(Collectors.groupingBy(CourseDetails::getPersonnelId));
    }

    /**
     * Calculate summary section with optimized logic
     */
    private OfficerSummaryResponseDto calculateSummarySection(
            List<Personnel> personnelList,
            Map<Long, List<CourseDetails>> unitCoursesMap,
            Set<Integer> allUnitCourseIds) {

        // Collect dates
        List<LocalDate> seniorityDates = new ArrayList<>();
        List<LocalDate> commissionDates = new ArrayList<>();
        Set<Integer> enrolledCourseIds = new HashSet<>();

        for (Personnel p : personnelList) {
            if (p == null) continue;

            // Safely add dates
            Optional.ofNullable(p.getDateOfSeniority()).ifPresent(seniorityDates::add);
            Optional.ofNullable(p.getDateOfCommission()).ifPresent(commissionDates::add);

            // Add enrolled course IDs
            List<CourseDetails> unitCourses = unitCoursesMap.getOrDefault(p.getId(), Collections.emptyList());
            unitCourses.stream()
                    .map(CourseDetails::getCourseId)
                    .filter(Objects::nonNull)
                    .forEach(enrolledCourseIds::add);
        }

        // Calculate min/max dates safely
        LocalDate earliestSeniority = seniorityDates.stream().min(LocalDate::compareTo).orElse(null);
        LocalDate latestSeniority = seniorityDates.stream().max(LocalDate::compareTo).orElse(null);
        LocalDate earliestCommission = commissionDates.stream().min(LocalDate::compareTo).orElse(null);
        LocalDate latestCommission = commissionDates.stream().max(LocalDate::compareTo).orElse(null);

        return OfficerSummaryResponseDto.builder()
                .totalOfficers(personnelList.size())
                .earliestSeniority(earliestSeniority)
                .latestSeniority(latestSeniority)
                .totalCoursesDone(enrolledCourseIds.size())
                .earliestCommission(earliestCommission)
                .latestCommission(latestCommission)
                .coursesTrainingYr(enrolledCourseIds.size())
                .coursesInUnit(allUnitCourseIds.size())
                .build();
    }

    /**
     * Prepare officer details for table section
     */
    private List<OfficerDetailResponseDto> prepareOfficerDetails(
            List<Personnel> personnelList,
            Map<Long, List<CourseDetails>> allCoursesMap,
            Map<Long, List<CourseDetails>> unitCoursesMap) {

        List<OfficerDetailResponseDto> officerDetails = new ArrayList<>();

        for (Personnel p : personnelList) {
            if (p == null || p.getId() == null) continue;

            try {
                List<CourseDetails> allCourses = allCoursesMap.getOrDefault(p.getId(), Collections.emptyList());
                List<CourseDetails> unitCourses = unitCoursesMap.getOrDefault(p.getId(), Collections.emptyList());

                OfficerDetailResponseDto officer = OfficerDetailResponseDto.builder()
                        .armyNo(p.getArmyNo())
                        .rank(p.getRank())
                        .fullName(p.getFullName())
                        .gender(p.getGender())
                        .dateOfBirth(p.getDateOfBirth())
                        .dateOfCommission(p.getDateOfCommission())
                        .dateOfSeniority(p.getDateOfSeniority())
                        .coursesDone(allCourses.size())
                        .trainingYr(allCourses.size())
                        .coursesInUnit(unitCourses.size())
                        .build();

                officerDetails.add(officer);

            } catch (Exception e) {
                log.error("Error processing officer {}: {}", p.getArmyNo(), e.getMessage());
                // Continue with next officer
            }
        }

        return officerDetails;
    }

    /**
     * Build empty response
     */
    private OfficerListResponseDto buildEmptyResponse(String formationType, String unitName) {
        OfficerSummaryResponseDto emptySummary = OfficerSummaryResponseDto.builder()
                .totalOfficers(0)
                .earliestSeniority(null)
                .latestSeniority(null)
                .totalCoursesDone(0)
                .earliestCommission(null)
                .latestCommission(null)
                .coursesTrainingYr(0)
                .coursesInUnit(0)
                .build();

        return OfficerListResponseDto.builder()
                .formationType(formationType)
                .unitName(unitName)
                .summary(emptySummary)
                .officers(Collections.emptyList())
                .build();
    }

    /**
     * Build error response
     */
    private OfficerListResponseDto buildErrorResponse(String formationType, String unitName, String error) {
        log.error("Returning error response for {}-{}: {}", formationType, unitName, error);

        OfficerSummaryResponseDto errorSummary = OfficerSummaryResponseDto.builder()
                .totalOfficers(0)
                .earliestSeniority(null)
                .latestSeniority(null)
                .totalCoursesDone(0)
                .earliestCommission(null)
                .latestCommission(null)
                .coursesTrainingYr(0)
                .coursesInUnit(0)
                .build();

        return OfficerListResponseDto.builder()
                .formationType(formationType)
                .unitName(unitName)
                .summary(errorSummary)
                .officers(Collections.emptyList())
                .build();
    }

    /**
     * Log summary for debugging
     */
    private void logSummary(OfficerSummaryResponseDto summary,
                            String formationType,
                            String unitName,
                            List<OfficerDetailResponseDto> officerDetails) {

        log.info("========== OFFICER SUMMARY for {} - {} ==========", formationType, unitName);
        log.info("Total Officers: {}", summary.getTotalOfficers());
        log.info("Total Courses Done (by current officers): {}", summary.getTotalCoursesDone());
        log.info("Courses in This Unit (historical): {}", summary.getCoursesInUnit());
        log.info("Earliest Seniority: {}", summary.getEarliestSeniority());
        log.info("Latest Seniority: {}", summary.getLatestSeniority());
        log.info("Officer Details Count: {}", officerDetails.size());
        log.info("=================================================");
    }
}