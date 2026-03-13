package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.UnitStatisticsDto;
import com.example.sena_bhawan.dto.UnitSummaryRequestDto;
import com.example.sena_bhawan.dto.UnitSummaryResponseDto;
import com.example.sena_bhawan.entity.CourseDetails;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.entity.PostingDetails;
import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.service.UnitSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitSummaryServiceImpl implements UnitSummaryService {

    private final PersonnelRepository personnelRepository;
    private final UnitSummaryRepository unitSummaryRepository;
    private final UnitCourseRepository unitCourseRepository;

    public UnitSummaryResponseDto getUnitSummary(UnitSummaryRequestDto requestDto) {
        log.info("Fetching unit summary for formation: {}, unit: {}",
                requestDto.getFormationType(), requestDto.getUnitName());

        try {
            String formationType = requestDto.getFormationType();
            String unitName = requestDto.getUnitName();

            // Step 1: Get all personnel IDs currently in this unit
            List<Long> personnelIds = unitSummaryRepository
                    .findPersonnelIdsByUnit(formationType, unitName);

            if (personnelIds.isEmpty()) {
                return buildEmptyResponse(formationType, unitName);
            }

            // Step 2: Fetch personnel details
            List<Personnel> personnelList = personnelRepository.findByIdIn(personnelIds);

            // Step 3: Get current postings in this unit
            List<PostingDetails> currentPostings = unitSummaryRepository
                    .findCurrentPostingsInUnit(formationType, unitName);

            // Step 4: Create map of personnel ID to their current posting period
            Map<Long, PostingDetails> currentPostingMap = currentPostings.stream()
                    .collect(Collectors.toMap(
                            PostingDetails::getPersonnelId,
                            posting -> posting,
                            (existing, replacement) -> existing
                    ));

            // Step 5: Get SOS dates (previous posting dates)
            Map<Long, LocalDate> sosMap = getSOSDates(personnelIds);

            // Step 6: Calculate statistics
            UnitStatisticsDto statistics = calculateStatistics(
                    personnelList,
                    personnelIds,
                    currentPostingMap,
                    sosMap,
                    formationType,  // Add formationType
                    unitName
            );

            // Step 7: Build response
            return UnitSummaryResponseDto.builder()
                    .formationType(formationType)
                    .unitName(unitName)
                    .statistics(statistics)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching unit summary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch unit summary: " + e.getMessage());
        }
    }

    private UnitStatisticsDto calculateStatistics(
            List<Personnel> personnelList,
            List<Long> personnelIds,
            Map<Long, PostingDetails> currentPostingMap,
            Map<Long, LocalDate> sosMap,
            String formationType,
            String unitName) {

        int totalOfficers = personnelList.size();

        // Get seniority dates
        List<LocalDate> seniorityDates = personnelList.stream()
                .map(Personnel::getDateOfSeniority)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        LocalDate earliestSeniority = seniorityDates.isEmpty() ? null : seniorityDates.get(0);
        LocalDate latestSeniority = seniorityDates.isEmpty() ? null : seniorityDates.get(seniorityDates.size() - 1);

        // Get commission dates
        List<LocalDate> commissionDates = personnelList.stream()
                .map(Personnel::getDateOfCommission)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        LocalDate earliestCommission = commissionDates.isEmpty() ? null : commissionDates.get(0);
        LocalDate latestCommission = commissionDates.isEmpty() ? null : commissionDates.get(commissionDates.size() - 1);

        // Calculate total courses done in this unit
        int totalCoursesDone = calculateTotalCoursesInUnit(personnelIds, currentPostingMap);

        // 🔥 FIXED: Calculate training year courses (current year)
        int currentYear = LocalDate.now().getYear();
        int coursesTrainingYr = calculateTrainingYearCourses(personnelIds, currentYear, currentPostingMap);

        log.info("Training Year Courses count: {} for year: {}", coursesTrainingYr, currentYear);

        // 🔥 NEW: Calculate total courses available in this unit (from course_details)
        int coursesInUnit = calculateCoursesAvailableInUnit(personnelIds, currentPostingMap);

        // 🔥 SOS statistics (if needed in future)
        // LocalDate earliestSOS = sosMap.values().stream().min(LocalDate::compareTo).orElse(null);
        // LocalDate latestSOS = sosMap.values().stream().max(LocalDate::compareTo).orElse(null);

        return UnitStatisticsDto.builder()
                .totalOfficers(totalOfficers)
                .earliestSeniority(earliestSeniority)
                .latestSeniority(latestSeniority)
                .totalCoursesDone(totalCoursesDone)
                .earliestCommission(earliestCommission)
                .latestCommission(latestCommission)
                // ✅ FIXED: Don't filter out zero values, show them
                .coursesTrainingYr(coursesTrainingYr)
                .coursesInUnit(coursesInUnit)
                .build();
    }

    private int calculateTotalCoursesInUnit(
            List<Long> personnelIds,
            Map<Long, PostingDetails> currentPostingMap) {

        int totalCourses = 0;

        for (Map.Entry<Long, PostingDetails> entry : currentPostingMap.entrySet()) {
            Long personnelId = entry.getKey();
            PostingDetails posting = entry.getValue();

            LocalDate fromDate = posting.getFromDate();
            LocalDate toDate = posting.getToDate() != null ? posting.getToDate() : LocalDate.now();

            List<CourseDetails> courses = unitCourseRepository
                    .findCoursesInUnitForPersonnel(Collections.singletonList(personnelId), fromDate, toDate);

            totalCourses += courses.size();

            log.debug("Personnel {}: {} courses in unit", personnelId, courses.size());
        }

        log.info("Total courses done in unit: {}", totalCourses);
        return totalCourses;
    }

    private int calculateTrainingYearCourses(
            List<Long> personnelIds,
            int year,
            Map<Long, PostingDetails> currentPostingMap) {

        int totalTrainingCourses = 0;

        for (Map.Entry<Long, PostingDetails> entry : currentPostingMap.entrySet()) {
            Long personnelId = entry.getKey();
            PostingDetails posting = entry.getValue();

            LocalDate fromDate = posting.getFromDate();
            LocalDate toDate = posting.getToDate() != null ? posting.getToDate() : LocalDate.now();

            // Count courses that were done in the target year
            List<CourseDetails> courses = unitCourseRepository
                    .findCoursesByYear(Collections.singletonList(personnelId), year);

            // 🔥 IMPORTANT: Filter courses that fall within posting period
            long coursesInYear = courses.stream()
                    .filter(c -> c.getFromDate() != null)
                    .filter(c -> !c.getFromDate().isBefore(fromDate) &&
                            !c.getFromDate().isAfter(toDate))
                    .count();

            totalTrainingCourses += coursesInYear;

            log.debug("Personnel {}: {} courses in year {} within posting period",
                    personnelId, coursesInYear, year);
        }

        log.info("Total training year courses: {} for year: {}", totalTrainingCourses, year);
        return totalTrainingCourses;
    }

    private int calculateCoursesAvailableInUnit(
            List<Long> personnelIds,
            Map<Long, PostingDetails> currentPostingMap) {

        // 🔥 This calculates total distinct courses done in this unit
        // You can modify this based on your business logic

        Set<Integer> uniqueCourseIds = new HashSet<>();

        for (Map.Entry<Long, PostingDetails> entry : currentPostingMap.entrySet()) {
            Long personnelId = entry.getKey();
            PostingDetails posting = entry.getValue();

            LocalDate fromDate = posting.getFromDate();
            LocalDate toDate = posting.getToDate() != null ? posting.getToDate() : LocalDate.now();

            List<CourseDetails> courses = unitCourseRepository
                    .findCoursesInUnitForPersonnel(Collections.singletonList(personnelId), fromDate, toDate);

            courses.forEach(course -> uniqueCourseIds.add(course.getCourseId()));
        }

        int availableCourses = uniqueCourseIds.size();
        log.info("Total distinct courses available in unit: {}", availableCourses);

        return availableCourses > 0 ? availableCourses : 12; // Fallback to 12 if no courses
    }

    private Map<Long, LocalDate> getSOSDates(List<Long> personnelIds) {
        Map<Long, LocalDate> sosMap = new HashMap<>();

        List<Object[]> previousPostings = unitSummaryRepository
                .findPreviousPostingDates(personnelIds);

        for (Object[] row : previousPostings) {
            Long personnelId = (Long) row[0];
            LocalDate fromDate = (LocalDate) row[1];

            // Only set SOS if not already set (takes the most recent previous posting)
            sosMap.putIfAbsent(personnelId, fromDate);
        }

        log.debug("Found SOS dates for {} personnel", sosMap.size());
        return sosMap;
    }

    private UnitSummaryResponseDto buildEmptyResponse(String formationType, String unitName) {
        return UnitSummaryResponseDto.builder()
                .formationType(formationType)
                .unitName(unitName)
                .statistics(UnitStatisticsDto.builder()
                        .totalOfficers(0)
                        .totalCoursesDone(0)
                        .coursesTrainingYr(0)  // Show 0 instead of null
                        .coursesInUnit(12)      // Default value
                        .earliestSeniority(null)
                        .latestSeniority(null)
                        .earliestCommission(null)
                        .latestCommission(null)
                        .build())
                .build();
    }
}