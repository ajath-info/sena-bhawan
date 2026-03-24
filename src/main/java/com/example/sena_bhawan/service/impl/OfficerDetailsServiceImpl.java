package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.service.OfficerDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfficerDetailsServiceImpl implements OfficerDetailsService {

    private final PostingDetailsRepository postingDetailsRepository;
    private final PersonnelRepository personnelRepository;
    private final CoursePanelRepository coursePanelNominationRepository;
    private final CourseScheduleRepository courseScheduleRepository;
    private final CourseMasterRepository courseMasterRepository;

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
            // Step 1: Get current personnel IDs with their current unit validation
            List<CurrentPostingDto> currentPostings = fetchCurrentPersonnelWithPostingInfo(formationType, unitName);
            if (CollectionUtils.isEmpty(currentPostings)) {
                return buildEmptyResponse(formationType, unitName);
            }

            List<Long> personnelIds = currentPostings.stream()
                    .map(CurrentPostingDto::getPersonnelId)
                    .collect(Collectors.toList());

            // Step 2: Fetch personnel details
            List<Personnel> personnelList = fetchPersonnelDetails(personnelIds);
            if (personnelList.isEmpty()) {
                return buildEmptyResponse(formationType, unitName);
            }

            // Step 3: Fetch all course nominations for these personnel
            List<CoursePanelNomination> allNominations = fetchAllNominations(personnelIds);

            // Step 4: Fetch course schedules and masters for all nominations
            Set<Long> scheduleIds = allNominations.stream()
                    .map(CoursePanelNomination::getScheduleId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Map<Long, CourseSchedule> scheduleMap = fetchCourseSchedules(scheduleIds);
            Map<Integer, CourseMaster> courseMasterMap = fetchCourseMasters(scheduleMap);

            // Step 5: Fetch unit-specific course nominations with posting validation
            List<CoursePanelNomination> unitNominations = fetchUnitNominations(currentPostings);

            // Step 6: Create optimized data structures
            Map<Long, List<CoursePanelNomination>> allNominationsMap = createNominationsMap(allNominations);
            Map<Long, List<CoursePanelNomination>> unitNominationsMap = createUnitNominationsMap(unitNominations);

            // Step 7: Calculate current financial year
            FinancialYearInfo currentFinancialYear = getCurrentFinancialYear();

            // Step 8: Prepare officer details for table
            List<OfficerDetailResponseDto> officerDetails = prepareOfficerDetails(
                    personnelList, allNominationsMap, unitNominationsMap,
                    scheduleMap, courseMasterMap, currentFinancialYear);

            // Step 9: Calculate summary
            OfficerSummaryResponseDto summary = calculateSummarySection(officerDetails);

            // Step 10: Log summary
            logSummary(summary, formationType, unitName, officerDetails);

            // Step 11: Build response
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
     * Fetch current personnel with their posting information
     * Logic:
     * - If status = "posted" AND tos_updated_date != null → current posting
     * - If status = "under_posting" AND tos_updated_date = null → current posting
     */
    private List<CurrentPostingDto> fetchCurrentPersonnelWithPostingInfo(String formationType, String unitName) {
        try {
            List<PostingDetails> allPostings = postingDetailsRepository
                    .findByFormationTypeAndUnitName(formationType, unitName);

            if (CollectionUtils.isEmpty(allPostings)) {
                log.info("No postings found for unit: {}", unitName);
                return Collections.emptyList();
            }

            // Group by personnelId to find the current posting for each
            Map<Long, List<PostingDetails>> postingsByPersonnel = allPostings.stream()
                    .filter(p -> p != null && p.getPersonnelId() != null)
                    .collect(Collectors.groupingBy(PostingDetails::getPersonnelId));

            List<CurrentPostingDto> currentPostings = new ArrayList<>();

            for (Map.Entry<Long, List<PostingDetails>> entry : postingsByPersonnel.entrySet()) {
                Long personnelId = entry.getKey();
                List<PostingDetails> personPostings = entry.getValue();

                // Find the current posting based on the logic
                PostingDetails currentPosting = findCurrentPosting(personPostings);

                if (currentPosting != null) {
                    CurrentPostingDto dto = new CurrentPostingDto();
                    dto.setPersonnelId(personnelId);
                    dto.setPostingId(currentPosting.getPostingId());
                    dto.setUnitName(currentPosting.getUnitName());
                    dto.setFormationType(currentPosting.getFormationType());
                    dto.setFromDate(currentPosting.getFromDate());
                    dto.setTosUpdatedDate(currentPosting.getTosUpdatedDate());
                    dto.setStatus(currentPosting.getStatus());
                    currentPostings.add(dto);
                }
            }

            log.info("Found {} personnel with current postings in unit", currentPostings.size());
            return currentPostings;

        } catch (Exception e) {
            log.error("Error fetching current postings: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Find current posting from list of postings for a personnel
     */
    private PostingDetails findCurrentPosting(List<PostingDetails> postings) {
        if (CollectionUtils.isEmpty(postings)) {
            return null;
        }

        // Sort by from_date descending to get latest first
        List<PostingDetails> sortedPostings = postings.stream()
                .sorted(Comparator.comparing(PostingDetails::getFromDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        for (PostingDetails posting : sortedPostings) {
            String status = posting.getStatus();
            LocalDate tosUpdatedDate = posting.getTosUpdatedDate();

            // Condition 1: status = "posted" AND tos_updated_date != null
            if ("posted".equalsIgnoreCase(status) && tosUpdatedDate != null) {
                return posting;
            }

            // Condition 2: status = "under_posting" AND tos_updated_date = null
            if ("under_posting".equalsIgnoreCase(status) && tosUpdatedDate == null) {
                return posting;
            }
        }

        // If no current posting found by above logic, return the latest one
        return sortedPostings.isEmpty() ? null : sortedPostings.get(0);
    }

    /**
     * Fetch all course nominations for given personnel IDs
     */
    private List<CoursePanelNomination> fetchAllNominations(List<Long> personnelIds) {
        try {
            if (CollectionUtils.isEmpty(personnelIds)) {
                return Collections.emptyList();
            }
            return coursePanelNominationRepository.findByPersonnelIdIn(personnelIds);
        } catch (Exception e) {
            log.error("Error fetching nominations: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetch course schedules by schedule IDs
     */
    private Map<Long, CourseSchedule> fetchCourseSchedules(Set<Long> scheduleIds) {
        try {
            if (CollectionUtils.isEmpty(scheduleIds)) {
                return Collections.emptyMap();
            }
            List<CourseSchedule> schedules = courseScheduleRepository.findByScheduleIdIn(scheduleIds);
            return schedules.stream()
                    .filter(s -> s != null && s.getScheduleId() != null)
                    .collect(Collectors.toMap(
                            CourseSchedule::getScheduleId,
                            s -> s,
                            (s1, s2) -> s1
                    ));
        } catch (Exception e) {
            log.error("Error fetching schedules: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Fetch course masters by course IDs
     */
    private Map<Integer, CourseMaster> fetchCourseMasters(Map<Long, CourseSchedule> scheduleMap) {
        try {
            Set<Integer> courseIds = scheduleMap.values().stream()
                    .map(s -> s.getCourse() != null ? s.getCourse().getSrno() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(courseIds)) {
                return Collections.emptyMap();
            }

            List<CourseMaster> courseMasters = courseMasterRepository.findBySrnoIn(courseIds);
            return courseMasters.stream()
                    .filter(c -> c != null && c.getSrno() != null)
                    .collect(Collectors.toMap(
                            CourseMaster::getSrno,
                            c -> c,
                            (c1, c2) -> c1
                    ));
        } catch (Exception e) {
            log.error("Error fetching course masters: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Fetch unit-specific nominations (courses done in current unit)
     * Based on the posting validation logic and course dates
     */
    private List<CoursePanelNomination> fetchUnitNominations(List<CurrentPostingDto> currentPostings) {
        try {
            if (CollectionUtils.isEmpty(currentPostings)) {
                return Collections.emptyList();
            }

            List<Long> personnelIds = currentPostings.stream()
                    .map(CurrentPostingDto::getPersonnelId)
                    .collect(Collectors.toList());

            // Get all nominations for these personnel
            List<CoursePanelNomination> allNominations = fetchAllNominations(personnelIds);

            if (CollectionUtils.isEmpty(allNominations)) {
                return Collections.emptyList();
            }

            // Get all schedule IDs
            Set<Long> scheduleIds = allNominations.stream()
                    .map(CoursePanelNomination::getScheduleId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Fetch schedules
            Map<Long, CourseSchedule> scheduleMap = fetchCourseSchedules(scheduleIds);

            // Create map of personnel ID to their current posting details
            Map<Long, CurrentPostingDto> personnelCurrentPostingMap = currentPostings.stream()
                    .collect(Collectors.toMap(
                            CurrentPostingDto::getPersonnelId,
                            p -> p,
                            (p1, p2) -> p1
                    ));

            // Filter nominations where course date falls within the personnel's current unit posting period
            List<CoursePanelNomination> unitNominations = new ArrayList<>();

            for (CoursePanelNomination nomination : allNominations) {
                Long personnelId = nomination.getPersonnelId();
                CurrentPostingDto currentPosting = personnelCurrentPostingMap.get(personnelId);

                if (currentPosting == null) {
                    continue;
                }

                CourseSchedule schedule = scheduleMap.get(nomination.getScheduleId());
                if (schedule == null || schedule.getStartDate() == null) {
                    continue;
                }

                // Check if course start date falls within the current posting period
                LocalDate courseStartDate = schedule.getStartDate();
                LocalDate postingFromDate = currentPosting.getFromDate();

                // If posting from date is null, assume course is done in current unit
                if (postingFromDate == null) {
                    unitNominations.add(nomination);
                } else if (!courseStartDate.isBefore(postingFromDate)) {
                    unitNominations.add(nomination);
                }
            }

            log.info("Found {} unit-specific nominations out of {} total",
                    unitNominations.size(), allNominations.size());

            return unitNominations;

        } catch (Exception e) {
            log.error("Error fetching unit nominations: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Create map of personnel ID to their nominations
     */
    private Map<Long, List<CoursePanelNomination>> createNominationsMap(List<CoursePanelNomination> nominations) {
        if (CollectionUtils.isEmpty(nominations)) {
            return Collections.emptyMap();
        }

        return nominations.stream()
                .filter(n -> n != null && n.getPersonnelId() != null)
                .collect(Collectors.groupingBy(CoursePanelNomination::getPersonnelId));
    }

    /**
     * Create map of personnel ID to their unit nominations
     */
    private Map<Long, List<CoursePanelNomination>> createUnitNominationsMap(List<CoursePanelNomination> unitNominations) {
        if (CollectionUtils.isEmpty(unitNominations)) {
            return Collections.emptyMap();
        }

        return unitNominations.stream()
                .filter(n -> n != null && n.getPersonnelId() != null)
                .collect(Collectors.groupingBy(CoursePanelNomination::getPersonnelId));
    }

    /**
     * Get current financial year info (e.g., FY 2024-25)
     */
    private FinancialYearInfo getCurrentFinancialYear() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int financialYearStartYear;

        // Financial year starts from April
        if (today.getMonthValue() >= 4) {
            financialYearStartYear = currentYear;
        } else {
            financialYearStartYear = currentYear - 1;
        }

        LocalDate financialYearStart = LocalDate.of(financialYearStartYear, 4, 1);
        LocalDate financialYearEnd = LocalDate.of(financialYearStartYear + 1, 3, 31);

        FinancialYearInfo info = new FinancialYearInfo();
        info.setStartDate(financialYearStart);
        info.setEndDate(financialYearEnd);
        info.setYearDisplay(financialYearStartYear + "-" + (financialYearStartYear + 1));

        return info;
    }

    /**
     * Check if a course falls within the financial year
     */
    private boolean isCourseInFinancialYear(CoursePanelNomination nomination,
                                            Map<Long, CourseSchedule> scheduleMap,
                                            FinancialYearInfo financialYear) {
        if (nomination == null || nomination.getScheduleId() == null) {
            return false;
        }

        CourseSchedule schedule = scheduleMap.get(nomination.getScheduleId());
        if (schedule == null || schedule.getStartDate() == null) {
            return false;
        }

        LocalDate courseDate = schedule.getStartDate();
        return !courseDate.isBefore(financialYear.getStartDate()) &&
                !courseDate.isAfter(financialYear.getEndDate());
    }

    /**
     * Prepare officer details for table section
     */
    private List<OfficerDetailResponseDto> prepareOfficerDetails(
            List<Personnel> personnelList,
            Map<Long, List<CoursePanelNomination>> allNominationsMap,
            Map<Long, List<CoursePanelNomination>> unitNominationsMap,
            Map<Long, CourseSchedule> scheduleMap,
            Map<Integer, CourseMaster> courseMasterMap,
            FinancialYearInfo currentFinancialYear) {

        List<OfficerDetailResponseDto> officerDetails = new ArrayList<>();

        for (Personnel p : personnelList) {
            if (p == null || p.getId() == null) continue;

            try {
                List<CoursePanelNomination> allNominations = allNominationsMap.getOrDefault(p.getId(), Collections.emptyList());
                List<CoursePanelNomination> unitNominations = unitNominationsMap.getOrDefault(p.getId(), Collections.emptyList());

                // 1. Courses Done - Total nominations count
                int coursesDone = allNominations.size();

                // 2. Training Year - Count nominations in current financial year
                int trainingYearCourses = 0;
                for (CoursePanelNomination nomination : allNominations) {
                    if (isCourseInFinancialYear(nomination, scheduleMap, currentFinancialYear)) {
                        trainingYearCourses++;
                    }
                }

                // 3. Courses in Unit - Count unit nominations
                int coursesInUnit = unitNominations.size();

                OfficerDetailResponseDto officer = OfficerDetailResponseDto.builder()
                        .armyNo(p.getArmyNo())
                        .rank(p.getRank())
                        .fullName(p.getFullName())
                        .gender(p.getGender())
                        .dateOfBirth(p.getDateOfBirth())
                        .dateOfCommission(p.getDateOfCommission())
                        .dateOfSeniority(p.getDateOfSeniority())
                        .coursesDone(coursesDone)
                        .trainingYr(trainingYearCourses)
                        .coursesInUnit(coursesInUnit)
                        .build();

                officerDetails.add(officer);

                log.debug("Officer {} - Courses Done: {}, Training Yr: {}, Courses in Unit: {}",
                        p.getArmyNo(), coursesDone, trainingYearCourses, coursesInUnit);

            } catch (Exception e) {
                log.error("Error processing officer {}: {}", p.getArmyNo(), e.getMessage());
                // Continue with next officer
            }
        }

        return officerDetails;
    }

    /**
     * Calculate summary section from officer details
     */
    private OfficerSummaryResponseDto calculateSummarySection(List<OfficerDetailResponseDto> officerDetails) {
        if (CollectionUtils.isEmpty(officerDetails)) {
            return OfficerSummaryResponseDto.builder()
                    .totalOfficers(0)
                    .totalCoursesDone(0)
                    .coursesTrainingYr(0)
                    .coursesInUnit(0)
                    .earliestSeniority(null)
                    .latestSeniority(null)
                    .earliestCommission(null)
                    .latestCommission(null)
                    .build();
        }

        int totalCoursesDone = officerDetails.stream()
                .mapToInt(OfficerDetailResponseDto::getCoursesDone)
                .sum();

        int totalTrainingYr = officerDetails.stream()
                .mapToInt(OfficerDetailResponseDto::getTrainingYr)
                .sum();

        int totalCoursesInUnit = officerDetails.stream()
                .mapToInt(OfficerDetailResponseDto::getCoursesInUnit)
                .sum();

        // Get min/max dates
        LocalDate earliestSeniority = officerDetails.stream()
                .map(OfficerDetailResponseDto::getDateOfSeniority)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate latestSeniority = officerDetails.stream()
                .map(OfficerDetailResponseDto::getDateOfSeniority)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        LocalDate earliestCommission = officerDetails.stream()
                .map(OfficerDetailResponseDto::getDateOfCommission)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate latestCommission = officerDetails.stream()
                .map(OfficerDetailResponseDto::getDateOfCommission)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        return OfficerSummaryResponseDto.builder()
                .totalOfficers(officerDetails.size())
                .totalCoursesDone(totalCoursesDone)
                .coursesTrainingYr(totalTrainingYr)
                .coursesInUnit(totalCoursesInUnit)
                .earliestSeniority(earliestSeniority)
                .latestSeniority(latestSeniority)
                .earliestCommission(earliestCommission)
                .latestCommission(latestCommission)
                .build();
    }

    /**
     * Fetch personnel details
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
        log.info("Courses (Training Yr): {}", summary.getCoursesTrainingYr());
        log.info("Courses in This Unit: {}", summary.getCoursesInUnit());
        log.info("Earliest Seniority: {}", summary.getEarliestSeniority());
        log.info("Latest Seniority: {}", summary.getLatestSeniority());
        log.info("Earliest Commission: {}", summary.getEarliestCommission());
        log.info("Latest Commission: {}", summary.getLatestCommission());
        log.info("Officer Details Count: {}", officerDetails.size());
        log.info("=================================================");
    }

    // Inner class for current posting DTO
    private static class CurrentPostingDto {
        private Long personnelId;
        private Long postingId;
        private String unitName;
        private String formationType;
        private LocalDate fromDate;
        private LocalDate tosUpdatedDate;
        private String status;

        // Getters and setters
        public Long getPersonnelId() { return personnelId; }
        public void setPersonnelId(Long personnelId) { this.personnelId = personnelId; }
        public Long getPostingId() { return postingId; }
        public void setPostingId(Long postingId) { this.postingId = postingId; }
        public String getUnitName() { return unitName; }
        public void setUnitName(String unitName) { this.unitName = unitName; }
        public String getFormationType() { return formationType; }
        public void setFormationType(String formationType) { this.formationType = formationType; }
        public LocalDate getFromDate() { return fromDate; }
        public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
        public LocalDate getTosUpdatedDate() { return tosUpdatedDate; }
        public void setTosUpdatedDate(LocalDate tosUpdatedDate) { this.tosUpdatedDate = tosUpdatedDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // Inner class for financial year info
    private static class FinancialYearInfo {
        private LocalDate startDate;
        private LocalDate endDate;
        private String yearDisplay;

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public String getYearDisplay() { return yearDisplay; }
        public void setYearDisplay(String yearDisplay) { this.yearDisplay = yearDisplay; }
    }
}