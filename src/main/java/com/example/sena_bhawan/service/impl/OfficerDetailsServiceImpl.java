package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.CourseDetails;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.entity.PostingDetails;
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

    // Financial year constants (1 April to 31 March)
    private static final int FINANCIAL_YEAR_START_MONTH = 4; // April
    private static final int FINANCIAL_YEAR_START_DAY = 1;
    private static final int FINANCIAL_YEAR_END_MONTH = 3; // March
    private static final int FINANCIAL_YEAR_END_DAY = 31;

    @Override
    public OfficerListResponseDto getOfficersByFormationAndUnit(OfficerListRequestDto requestDto) {
        try {
            String formationType = requestDto.getFormationType();
            String unitName = requestDto.getUnitName();

            log.info("Fetching officers for Formation: {}, Unit: {}", formationType, unitName);

            // Validate input
            if (formationType == null || unitName == null) {
                throw new IllegalArgumentException("Formation type and unit name cannot be null");
            }

            // Step 1: Get personnel IDs from posting_details (currently in this formation/unit)
            // This uses both formationType AND unitName to get current officers in selected unit/formation
            List<Long> personnelIds = postingDetailsRepository
                    .findPersonnelIdsByFormationTypeAndUnitName(formationType, unitName);

            if (CollectionUtils.isEmpty(personnelIds)) {
                log.info("No personnel found for Formation: {}, Unit: {}", formationType, unitName);
                return buildEmptyResponse(formationType, unitName);
            }

            // Step 2: Fetch personnel details
            List<Personnel> personnelList = personnelRepository.findByIdIn(personnelIds);

            if (CollectionUtils.isEmpty(personnelList)) {
                return buildEmptyResponse(formationType, unitName);
            }

            // Step 3: Fetch ALL courses for these personnel (entire service history)
            List<CourseDetails> allCourses = courseDetailsRepository.findByPersonnelIdIn(personnelIds);

            // Step 4: Fetch current postings in this formation/unit
            List<PostingDetails> currentPostings = postingDetailsRepository
                    .findPostingsByPersonnelIdsAndUnit(personnelIds, formationType, unitName);

            // Step 5: Fetch ALL postings for these personnel (to determine when they were in which unit)
            // This is needed to filter courses that were done specifically in this unit
            List<PostingDetails> allPostings = getAllPostingsForPersonnel(personnelIds);

            // Step 6: Create maps for efficient lookup
            Map<Long, List<CourseDetails>> allCoursesByPersonnel = createCoursesMap(allCourses);
            Map<Long, List<PostingDetails>> allPostingsByPersonnel = createPostingsMap(allPostings);

            // Step 7: Create map of current posting periods (for this specific formation/unit)
            Map<Long, PostingDetails> currentPostingMap = createCurrentPostingMap(currentPostings);

            // Step 8: Calculate SUMMARY section data with correct logic
            OfficerSummaryResponseDto summary = calculateSummarySection(
                    personnelList,
                    allCoursesByPersonnel,      // For total courses across service
                    currentPostingMap,           // For unit-specific filtering
                    allPostingsByPersonnel       // For complete posting history
            );

            // Step 9: Map to officer details for TABLE section with correct logic
            List<OfficerDetailResponseDto> officerDetails = mapToOfficerDetails(
                    personnelList,
                    allCoursesByPersonnel,       // For total courses across service
                    currentPostingMap,            // For unit-specific filtering
                    allPostingsByPersonnel        // For complete posting history
            );

            // Step 10: Log summary for debugging
            logSummary(summary, formationType, unitName);

            // Step 11: Build and return response
            return OfficerListResponseDto.builder()
                    .formationType(formationType)
                    .unitName(unitName)
                    .summary(summary)
                    .officers(officerDetails)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching officer details: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch officer details: " + e.getMessage());
        }
    }

    /**
     * Creates empty response when no data found
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
                .officers(new ArrayList<>())
                .build();
    }

    /**
     * Fetch all postings for given personnel IDs (complete service history)
     */
    private List<PostingDetails> getAllPostingsForPersonnel(List<Long> personnelIds) {
        if (CollectionUtils.isEmpty(personnelIds)) {
            return new ArrayList<>();
        }

        List<PostingDetails> allPostings = new ArrayList<>();
        for (Long personnelId : personnelIds) {
            List<PostingDetails> personPostings = postingDetailsRepository
                    .findByPersonnelIdOrderByFromDateDesc(personnelId);
            if (!CollectionUtils.isEmpty(personPostings)) {
                allPostings.addAll(personPostings);
            }
        }
        return allPostings;
    }

    /**
     * Creates map of personnel ID to their courses list (sorted by date)
     */
    private Map<Long, List<CourseDetails>> createCoursesMap(List<CourseDetails> courses) {
        if (CollectionUtils.isEmpty(courses)) {
            return new HashMap<>();
        }
        return courses.stream()
                .filter(c -> c != null && c.getPersonnelId() != null)
                .collect(Collectors.groupingBy(
                        CourseDetails::getPersonnelId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .filter(Objects::nonNull)
                                        .sorted(Comparator.comparing(CourseDetails::getFromDate,
                                                Comparator.nullsLast(Comparator.naturalOrder())))
                                        .collect(Collectors.toList())
                        )
                ));
    }

    /**
     * Creates map of personnel ID to their postings list (sorted by fromDate)
     */
    private Map<Long, List<PostingDetails>> createPostingsMap(List<PostingDetails> postings) {
        if (CollectionUtils.isEmpty(postings)) {
            return new HashMap<>();
        }
        return postings.stream()
                .filter(p -> p != null && p.getPersonnelId() != null)
                .collect(Collectors.groupingBy(
                        PostingDetails::getPersonnelId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .filter(Objects::nonNull)
                                        .sorted(Comparator.comparing(PostingDetails::getFromDate,
                                                Comparator.nullsLast(Comparator.naturalOrder())))
                                        .collect(Collectors.toList())
                        )
                ));
    }

    /**
     * Creates map of personnel ID to their current posting in selected formation/unit
     */
    private Map<Long, PostingDetails> createCurrentPostingMap(List<PostingDetails> postings) {
        if (CollectionUtils.isEmpty(postings)) {
            return new HashMap<>();
        }

        // For each personnel, get their latest posting in this formation/unit
        return postings.stream()
                .filter(p -> p != null && p.getPersonnelId() != null)
                .collect(Collectors.toMap(
                        PostingDetails::getPersonnelId,
                        posting -> posting,
                        (existing, replacement) -> {
                            // Keep the one with later fromDate (current posting)
                            if (existing.getFromDate() == null) return replacement;
                            if (replacement.getFromDate() == null) return existing;
                            return existing.getFromDate().isAfter(replacement.getFromDate()) ?
                                    existing : replacement;
                        }
                ));
    }

    /**
     * Calculates SUMMARY section data with correct logic as per UI
     *
     * UI Summary Section:
     * - Total Officers: Current officers in selected formation/unit
     * - Total Courses Done: ALL courses by personnel (entire service)
     * - Courses (Training Yr): Courses in current financial year (while in this unit)
     * - Courses in This Unit: Total courses done while in this unit (distinct course IDs)
     * - Earliest/Latest Seniority
     * - Earliest/Latest Commission
     */
    private OfficerSummaryResponseDto calculateSummarySection(
            List<Personnel> personnelList,
            Map<Long, List<CourseDetails>> allCoursesByPersonnel,
            Map<Long, PostingDetails> currentPostingMap,
            Map<Long, List<PostingDetails>> allPostingsByPersonnel) {

        int totalOfficers = personnelList.size();

        // Get current financial year range (1 April to 31 March)
        FinancialYearRange currentFY = getCurrentFinancialYear();

        // Collect seniority and commission dates
        List<LocalDate> seniorityDates = new ArrayList<>();
        List<LocalDate> commissionDates = new ArrayList<>();

        // Track statistics
        int totalCoursesAllService = 0;                 // Total Courses Done (entire service)
        int totalCoursesInCurrentUnit = 0;               // Courses in This Unit (actual count)
        int totalCoursesTrainingYr = 0;                   // Courses (Training Yr) - current FY in this unit
        Set<Integer> uniqueUnitCourseIds = new HashSet<>(); // For distinct courses in this unit

        for (Personnel p : personnelList) {
            if (p == null || p.getId() == null) continue;

            // Collect dates for summary
            if (p.getDateOfSeniority() != null) {
                seniorityDates.add(p.getDateOfSeniority());
            }
            if (p.getDateOfCommission() != null) {
                commissionDates.add(p.getDateOfCommission());
            }

            // Get ALL courses for this personnel (entire service)
            List<CourseDetails> personAllCourses = allCoursesByPersonnel.getOrDefault(p.getId(), new ArrayList<>());

            // Add to total courses count (entire service) - for "Total Courses Done"
            totalCoursesAllService += personAllCourses.size();

            // Get current posting in selected formation/unit
            PostingDetails currentPosting = currentPostingMap.get(p.getId());

            if (currentPosting != null) {
                // Get courses done during current posting period (in this unit)
                List<CourseDetails> currentUnitCourses = filterCoursesByPostingPeriod(
                        personAllCourses,
                        currentPosting
                );

                // Update unit-specific statistics
                totalCoursesInCurrentUnit += currentUnitCourses.size(); // For "Courses in This Unit" count

                // Track unique course IDs for "Courses in This Unit" (distinct courses)
                currentUnitCourses.forEach(c -> {
                    if (c.getCourseId() != null) {
                        uniqueUnitCourseIds.add(c.getCourseId());
                    }
                });

                // Count training year courses (current financial year in this unit) - for "Courses (Training Yr)"
                long trainingCount = currentUnitCourses.stream()
                        .filter(c -> isCourseInFinancialYear(c, currentFY))
                        .count();

                totalCoursesTrainingYr += trainingCount;
            }
        }

        // Calculate min/max dates
        LocalDate earliestSeniority = seniorityDates.stream().min(LocalDate::compareTo).orElse(null);
        LocalDate latestSeniority = seniorityDates.stream().max(LocalDate::compareTo).orElse(null);
        LocalDate earliestCommission = commissionDates.stream().min(LocalDate::compareTo).orElse(null);
        LocalDate latestCommission = commissionDates.stream().max(LocalDate::compareTo).orElse(null);

        // Build summary for UI with correct field mapping
        return OfficerSummaryResponseDto.builder()
                .totalOfficers(totalOfficers)                          // Current officers in unit
                .earliestSeniority(earliestSeniority)
                .latestSeniority(latestSeniority)
                .totalCoursesDone(totalCoursesAllService)             // ALL courses across service
                .earliestCommission(earliestCommission)
                .latestCommission(latestCommission)
                .coursesTrainingYr(totalCoursesTrainingYr)            // Current FY courses in this unit
                .coursesInUnit(uniqueUnitCourseIds.size())            // Distinct courses in this unit
                .build();
    }

    private boolean isCourseInFinancialYear(CourseDetails course, FinancialYearRange fyRange) {
        if (course == null) return false;

        LocalDate fromDate = course.getFromDate();
        LocalDate toDate = course.getToDate() != null ? course.getToDate() : fromDate;

        if (fromDate == null) return false;

        // Check if ANY PART of course falls in financial year
        return !(toDate.isBefore(fyRange.startDate) || fromDate.isAfter(fyRange.endDate));
    }
    /**
     * Maps personnel to officer details for TABLE section with correct logic
     *
     * Table Columns (as per UI):
     * - Army No, Rank, Name, Gender, DOB, Commission Date, Seniority Date
     * - Courses Done: TOTAL courses across entire service
     * - Training Yr: Current financial year courses in this unit
     * - Courses in Unit: Courses done in this unit only
     */
    private List<OfficerDetailResponseDto> mapToOfficerDetails(
            List<Personnel> personnelList,
            Map<Long, List<CourseDetails>> allCoursesByPersonnel,
            Map<Long, PostingDetails> currentPostingMap,
            Map<Long, List<PostingDetails>> allPostingsByPersonnel) {

        return personnelList.stream()
                .filter(Objects::nonNull)
                .map(p -> {
                    List<CourseDetails> allCourses = allCoursesByPersonnel.getOrDefault(p.getId(), new ArrayList<>());
                    PostingDetails currentPosting = currentPostingMap.get(p.getId());
                    List<PostingDetails> allPostings = allPostingsByPersonnel.getOrDefault(p.getId(), new ArrayList<>());
                    return mapToOfficerDetail(p, allCourses, currentPosting, allPostings);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Maps single personnel to officer detail DTO with correct field values
     */
    private OfficerDetailResponseDto mapToOfficerDetail(
            Personnel p,
            List<CourseDetails> allCourses,
            PostingDetails currentPosting,
            List<PostingDetails> allPostings) {

        if (p == null) return null;

        try {
            // Get current financial year range
            FinancialYearRange currentFY = getCurrentFinancialYear();

            // 1. Courses Done = TOTAL courses across entire service (for table)
            int totalCoursesCount = allCourses.size();

            // 2. Get courses done in current unit (for "Courses in Unit" column)
            List<CourseDetails> currentUnitCourses = new ArrayList<>();
            if (currentPosting != null) {
                currentUnitCourses = filterCoursesByPostingPeriod(allCourses, currentPosting);
            }
            int currentUnitCoursesCount = currentUnitCourses.size();

            // 3. Calculate training year courses (current financial year in current unit) - for "Training Yr" column
            int trainingYrCourses = 0;
            if (currentPosting != null) {
                 trainingYrCourses = (int)currentUnitCourses.stream()
                        .filter(c -> isCourseInFinancialYear(c, currentFY))
                        .count();
            }

            // Build officer detail for table display with correct field mapping
            return OfficerDetailResponseDto.builder()
                    .armyNo(p.getArmyNo())
                    .rank(p.getRank())
                    .fullName(p.getFullName())
                    .gender(p.getGender())
                    .dateOfBirth(p.getDateOfBirth())
                    .dateOfCommission(p.getDateOfCommission())
                    .dateOfSeniority(p.getDateOfSeniority())
                    // Courses Done = TOTAL courses across entire service
                    .coursesDone(totalCoursesCount)
                    // Training Yr = Current financial year courses in current unit
                    .trainingYr(trainingYrCourses)
                    // Courses in Unit = Courses done in current unit only
                    .coursesInUnit(currentUnitCoursesCount)
                    .build();

        } catch (Exception e) {
            log.error("Error mapping officer detail for personnel ID: {}", p.getId(), e);
            return null;
        }
    }

    /**
     * Filters courses by posting period
     */
    private List<CourseDetails> filterCoursesByPostingPeriod(
            List<CourseDetails> courses,
            PostingDetails posting) {

        if (posting == null || posting.getFromDate() == null) {
            return new ArrayList<>();
        }

        LocalDate fromDate = posting.getFromDate();
        LocalDate toDate = posting.getToDate() != null ?
                posting.getToDate() : LocalDate.now();

        return courses.stream()
                .filter(c -> c != null &&
                        c.getFromDate() != null &&
                        !c.getFromDate().isBefore(fromDate) &&
                        !c.getFromDate().isAfter(toDate))
                .collect(Collectors.toList());
    }

    /**
     * Inner class to represent financial year range (1 April to 31 March)
     */
    private static class FinancialYearRange {
        LocalDate startDate;
        LocalDate endDate;

        FinancialYearRange(LocalDate start, LocalDate end) {
            this.startDate = start;
            this.endDate = end;
        }
    }

    /**
     * Get current financial year range (1 April to 31 March)
     */
    private FinancialYearRange getCurrentFinancialYear() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();

        LocalDate startDate;
        LocalDate endDate;

        // If current date is before April 1, financial year is (previous_year-04-01 to current_year-03-31)
        if (now.getMonthValue() < FINANCIAL_YEAR_START_MONTH) {
            startDate = LocalDate.of(currentYear - 1, FINANCIAL_YEAR_START_MONTH, FINANCIAL_YEAR_START_DAY);
            endDate = LocalDate.of(currentYear, FINANCIAL_YEAR_END_MONTH, FINANCIAL_YEAR_END_DAY);
        }
        // If current date is on or after April 1, financial year is (current_year-04-01 to next_year-03-31)
        else {
            startDate = LocalDate.of(currentYear, FINANCIAL_YEAR_START_MONTH, FINANCIAL_YEAR_START_DAY);
            endDate = LocalDate.of(currentYear + 1, FINANCIAL_YEAR_END_MONTH, FINANCIAL_YEAR_END_DAY);
        }

        return new FinancialYearRange(startDate, endDate);
    }

    /**
     * Check if a date falls within a given financial year
     */
//    private boolean isDateInFinancialYear(LocalDate date, FinancialYearRange fyRange) {
//        if (date == null || fyRange == null) return false;
//        return !date.isBefore(fyRange.startDate) && !date.isAfter(fyRange.endDate);
//    }

    /**
     * Logs summary for debugging - updated to show correct field mapping
     */
    private void logSummary(OfficerSummaryResponseDto summary, String formationType, String unitName) {
        log.info("=== SUMMARY for {} - {} (Correct Logic) ===", formationType, unitName);
        log.info("Total Officers (Current in unit): {}", summary.getTotalOfficers());
        log.info("Total Courses Done (Entire Service - UI field): {}", summary.getTotalCoursesDone());
        log.info("Courses (Training Yr - Current FY in unit - UI field): {}", summary.getCoursesTrainingYr());
        log.info("Courses in This Unit (Distinct - UI field): {}", summary.getCoursesInUnit());
        log.info("Earliest Seniority: {}", summary.getEarliestSeniority());
        log.info("Latest Seniority: {}", summary.getLatestSeniority());
        log.info("Earliest Commission: {}", summary.getEarliestCommission());
        log.info("Latest Commission: {}", summary.getLatestCommission());
        log.info("================================");

        // Also log what each officer will show in table
        log.info("TABLE columns mapping:");
        log.info("- Courses Done: Total courses across entire service");
        log.info("- Training Yr: Current FY courses in current unit");
        log.info("- Courses in Unit: Courses done in current unit only");
    }
}