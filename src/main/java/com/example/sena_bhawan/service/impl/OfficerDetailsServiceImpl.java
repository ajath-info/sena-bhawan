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

            // Step 1: Get personnel IDs from posting_details (currently in this unit)
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

            // Step 3: Fetch all courses for these personnel
            List<CourseDetails> allCourses = courseDetailsRepository.findByPersonnelIdIn(personnelIds);

            // Step 4: Fetch current postings in this unit
            List<PostingDetails> currentPostings = postingDetailsRepository
                    .findPostingsByPersonnelIdsAndUnit(personnelIds, formationType, unitName);

            // Step 5: Create maps for efficient lookup
            Map<Long, List<CourseDetails>> coursesByPersonnel = createCoursesMap(allCourses);
            Map<Long, List<PostingDetails>> postingsByPersonnel = createPostingsMap(currentPostings);

            // Step 6: Create map of current posting periods
            Map<Long, PostingDetails> currentPostingMap = createCurrentPostingMap(currentPostings);

            // Step 7: Calculate SUMMARY section data (using UnitSummaryServiceImpl logic)
            OfficerSummaryResponseDto summary = calculateSummarySection(
                    personnelList,
                    personnelIds,
                    coursesByPersonnel,
                    currentPostingMap
            );

            // Step 8: Map to officer details for TABLE section
            List<OfficerDetailResponseDto> officerDetails = mapToOfficerDetails(
                    personnelList,
                    coursesByPersonnel,
                    postingsByPersonnel
            );

            // Step 9: Log summary for debugging
            logSummary(summary, formationType, unitName);

            // Step 10: Build and return response
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
     * Creates map of personnel ID to their courses list
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
     * Creates map of personnel ID to their current posting
     */
    private Map<Long, PostingDetails> createCurrentPostingMap(List<PostingDetails> postings) {
        if (CollectionUtils.isEmpty(postings)) {
            return new HashMap<>();
        }

        // For each personnel, get their latest posting in this unit
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
     * Calculates SUMMARY section data (as per UnitSummaryServiceImpl logic)
     *
     * UI Summary Section:
     * - Total Officers
     * - Total Courses Done (in current unit)
     * - Courses (Training Yr) - current year courses in current unit
     * - Courses in This Unit - total courses in current unit
     * - Earliest/Latest Seniority
     * - Earliest/Latest Commission
     */
    private OfficerSummaryResponseDto calculateSummarySection(
            List<Personnel> personnelList,
            List<Long> personnelIds,
            Map<Long, List<CourseDetails>> coursesByPersonnel,
            Map<Long, PostingDetails> currentPostingMap) {

        int totalOfficers = personnelList.size();
        int currentYear = LocalDate.now().getYear();

        // Collect seniority and commission dates
        List<LocalDate> seniorityDates = new ArrayList<>();
        List<LocalDate> commissionDates = new ArrayList<>();

        // Track courses statistics
        Set<Integer> uniqueCourseIds = new HashSet<>();  // For courses in this unit
        int totalCoursesInUnit = 0;                       // Total courses done in current unit
        int trainingYearCourses = 0;                       // Current year courses in current unit

        for (Personnel p : personnelList) {
            if (p == null || p.getId() == null) continue;

            // Collect dates
            if (p.getDateOfSeniority() != null) {
                seniorityDates.add(p.getDateOfSeniority());
            }
            if (p.getDateOfCommission() != null) {
                commissionDates.add(p.getDateOfCommission());
            }

            // Get current posting for this personnel
            PostingDetails currentPosting = currentPostingMap.get(p.getId());

            if (currentPosting != null) {
                // Get courses for this personnel
                List<CourseDetails> allPersonnelCourses = coursesByPersonnel.getOrDefault(p.getId(), new ArrayList<>());

                // Get courses done during current posting period
                List<CourseDetails> currentUnitCourses = filterCoursesByPostingPeriod(
                        allPersonnelCourses,
                        currentPosting
                );

                // Update statistics
                totalCoursesInUnit += currentUnitCourses.size();

                // Track unique course IDs for "Courses in This Unit"
                currentUnitCourses.forEach(c -> {
                    if (c.getCourseId() != null) {
                        uniqueCourseIds.add(c.getCourseId());
                    }
                });

                // Count training year courses (current year in current unit)
                long trainingCount = currentUnitCourses.stream()
                        .filter(c -> c.getFromDate() != null &&
                                c.getFromDate().getYear() == currentYear)
                        .count();

                trainingYearCourses += trainingCount;
            }
        }

        // Calculate min/max dates
        LocalDate earliestSeniority = seniorityDates.stream().min(LocalDate::compareTo).orElse(null);
        LocalDate latestSeniority = seniorityDates.stream().max(LocalDate::compareTo).orElse(null);
        LocalDate earliestCommission = commissionDates.stream().min(LocalDate::compareTo).orElse(null);
        LocalDate latestCommission = commissionDates.stream().max(LocalDate::compareTo).orElse(null);

        // Build summary for UI
        return OfficerSummaryResponseDto.builder()
                .totalOfficers(totalOfficers)
                .earliestSeniority(earliestSeniority)
                .latestSeniority(latestSeniority)
                // Total Courses Done = Courses done in current unit (as per UnitSummaryServiceImpl)
                .totalCoursesDone(totalCoursesInUnit)
                .earliestCommission(earliestCommission)
                .latestCommission(latestCommission)
                // Courses (Training Yr) = Current year courses in current unit
                .coursesTrainingYr(trainingYearCourses)
                // Courses in This Unit = Total courses in current unit (distinct course IDs)
                .coursesInUnit(uniqueCourseIds.size())
                .build();
    }

    /**
     * Maps personnel to officer details for TABLE section
     *
     * Table Columns:
     * - Army No, Rank, Name, Gender, DOB, Commission Date, Seniority Date
     * - Courses Done (in current unit)
     * - Training Yr (current year courses in current unit)
     * - Courses in Unit (same as Courses Done)
     */
    private List<OfficerDetailResponseDto> mapToOfficerDetails(
            List<Personnel> personnelList,
            Map<Long, List<CourseDetails>> coursesByPersonnel,
            Map<Long, List<PostingDetails>> postingsByPersonnel) {

        return personnelList.stream()
                .filter(Objects::nonNull)
                .map(p -> {
                    List<CourseDetails> allCourses = coursesByPersonnel.getOrDefault(p.getId(), new ArrayList<>());
                    List<PostingDetails> postings = postingsByPersonnel.getOrDefault(p.getId(), new ArrayList<>());
                    return mapToOfficerDetail(p, allCourses, postings);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Maps single personnel to officer detail DTO
     */
    private OfficerDetailResponseDto mapToOfficerDetail(
            Personnel p,
            List<CourseDetails> allCourses,
            List<PostingDetails> postings) {

        if (p == null) return null;

        try {
            // Get current unit courses (based on latest posting)
            List<CourseDetails> currentUnitCourses = getCurrentUnitCourses(allCourses, postings);
            int currentUnitCoursesCount = currentUnitCourses.size();

            // Calculate training year courses (current year in current unit)
            int currentYear = LocalDate.now().getYear();
            int trainingYrCourses = (int) currentUnitCourses.stream()
                    .filter(c -> c.getFromDate() != null &&
                            c.getFromDate().getYear() == currentYear)
                    .count();

            // Build officer detail for table display
            return OfficerDetailResponseDto.builder()
                    .armyNo(p.getArmyNo())
                    .rank(p.getRank())
                    .fullName(p.getFullName())
                    .gender(p.getGender())
                    .dateOfBirth(p.getDateOfBirth())
                    .dateOfCommission(p.getDateOfCommission())
                    .dateOfSeniority(p.getDateOfSeniority())
                    // Courses Done = Current unit courses only
                    .coursesDone(currentUnitCoursesCount)
                    // Training Yr = Current year courses in current unit
                    .trainingYr(trainingYrCourses)
                    // Courses in Unit = Same as Courses Done
                    .coursesInUnit(currentUnitCoursesCount)
                    .build();

        } catch (Exception e) {
            log.error("Error mapping officer detail for personnel ID: {}", p.getId(), e);
            return null;
        }
    }

    /**
     * Gets courses done during current unit's posting period
     */
    private List<CourseDetails> getCurrentUnitCourses(
            List<CourseDetails> allCourses,
            List<PostingDetails> postings) {

        if (CollectionUtils.isEmpty(postings) || CollectionUtils.isEmpty(allCourses)) {
            return new ArrayList<>();
        }

        // Get the latest posting in current unit
        PostingDetails latestPosting = postings.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(PostingDetails::getFromDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        if (latestPosting == null) {
            return new ArrayList<>();
        }

        return filterCoursesByPostingPeriod(allCourses, latestPosting);
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
     * Logs summary for debugging
     */
    private void logSummary(OfficerSummaryResponseDto summary, String formationType, String unitName) {
        log.info("=== SUMMARY for {} - {} ===", formationType, unitName);
        log.info("Total Officers: {}", summary.getTotalOfficers());
        log.info("Total Courses Done (in unit): {}", summary.getTotalCoursesDone());
        log.info("Courses (Training Yr): {}", summary.getCoursesTrainingYr());
        log.info("Courses in This Unit: {}", summary.getCoursesInUnit());
        log.info("Earliest Seniority: {}", summary.getEarliestSeniority());
        log.info("Latest Seniority: {}", summary.getLatestSeniority());
        log.info("Earliest Commission: {}", summary.getEarliestCommission());
        log.info("Latest Commission: {}", summary.getLatestCommission());
        log.info("================================");
    }
}