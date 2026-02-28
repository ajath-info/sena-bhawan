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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

            // Step 1: Get personnel IDs from posting_details
            List<Long> personnelIds = postingDetailsRepository
                    .findPersonnelIdsByFormationTypeAndUnitName(formationType, unitName);

            if (personnelIds.isEmpty()) {
                return OfficerListResponseDto.builder()
                        .formationType(formationType)
                        .unitName(unitName)
                        .summary(null)
                        .officers(new ArrayList<>())
                        .build();
            }

            // Step 2: Fetch personnel details
            List<Personnel> personnelList = personnelRepository.findByIdIn(personnelIds);

            // Step 3: Fetch all courses for these personnel
            List<CourseDetails> allCourses = courseDetailsRepository
                    .findByPersonnelIdIn(personnelIds);

            // Group courses by personnel ID
            Map<Long, List<CourseDetails>> coursesByPersonnel = allCourses.stream()
                    .collect(Collectors.groupingBy(CourseDetails::getPersonnelId));

            // Step 4: Fetch posting details for current unit
            List<PostingDetails> unitPostings = postingDetailsRepository
                    .findPostingsByPersonnelIdsAndUnit(personnelIds, formationType, unitName);

            // Group postings by personnel ID
            Map<Long, List<PostingDetails>> postingsByPersonnel = unitPostings.stream()
                    .collect(Collectors.groupingBy(PostingDetails::getPersonnelId));

            // Step 5: Calculate summary
            OfficerSummaryResponseDto summary = calculateSummary(
                    personnelList,
                    coursesByPersonnel,
                    postingsByPersonnel,
                    formationType,
                    unitName
            );

            // Step 6: Map to officer details
            List<OfficerDetailResponseDto> officerDetails = personnelList.stream()
                    .map(p -> mapToOfficerDetail(
                            p,
                            coursesByPersonnel.getOrDefault(p.getId(), new ArrayList<>()),
                            postingsByPersonnel.getOrDefault(p.getId(), new ArrayList<>())
                    ))
                    .collect(Collectors.toList());

            // Step 7: Build response
            return OfficerListResponseDto.builder()
                    .formationType(formationType)
                    .unitName(unitName)
                    .summary(summary)
                    .officers(officerDetails)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching officer details: " + e.getMessage());
        }
    }

    private OfficerSummaryResponseDto calculateSummary(
            List<Personnel> personnelList,
            Map<Long, List<CourseDetails>> coursesByPersonnel,
            Map<Long, List<PostingDetails>> postingsByPersonnel,
            String formationType,
            String unitName) {

        int totalOfficers = personnelList.size();
        int totalCoursesDone = 0;
        int coursesTrainingYr = 0;
        int coursesInUnit = 0;

        int currentYear = LocalDate.now().getYear();
        List<Long> personnelIds = personnelList.stream()
                .map(Personnel::getId)
                .collect(Collectors.toList());

        // Calculate course statistics
        for (Personnel p : personnelList) {
            List<CourseDetails> courses = coursesByPersonnel.getOrDefault(p.getId(), new ArrayList<>());
            totalCoursesDone += courses.size();

            // Training year courses (current year)
            coursesTrainingYr += (int) courses.stream()
                    .filter(c -> c.getFromDate() != null &&
                            c.getFromDate().getYear() == currentYear)
                    .count();

            // Courses in current unit
            List<PostingDetails> postings = postingsByPersonnel.getOrDefault(p.getId(), new ArrayList<>());
            coursesInUnit += calculateCoursesInUnit(p.getId(), courses, postings);
        }

        // Get date statistics
        LocalDate earliestSeniority = personnelRepository
                .findEarliestSeniority(personnelIds).orElse(null);
        LocalDate latestSeniority = personnelRepository
                .findLatestSeniority(personnelIds).orElse(null);
        LocalDate earliestCommission = personnelRepository
                .findEarliestCommission(personnelIds).orElse(null);
        LocalDate latestCommission = personnelRepository
                .findLatestCommission(personnelIds).orElse(null);

        return OfficerSummaryResponseDto.builder()
                .totalOfficers(totalOfficers)
                .earliestSeniority(earliestSeniority)
                .latestSeniority(latestSeniority)
                .totalCoursesDone(totalCoursesDone)
                .earliestCommission(earliestCommission)
                .latestCommission(latestCommission)
                .coursesTrainingYr(coursesTrainingYr)
                .coursesInUnit(coursesInUnit)
                .build();
    }

    private OfficerDetailResponseDto mapToOfficerDetail(
            Personnel p,
            List<CourseDetails> courses,
            List<PostingDetails> postings) {

        int coursesDone = courses.size();

        // Training year courses (current year)
        int currentYear = LocalDate.now().getYear();
        int trainingYr = (int) courses.stream()
                .filter(c -> c.getFromDate() != null &&
                        c.getFromDate().getYear() == currentYear)
                .count();

        // Courses in current unit
        int coursesInUnit = calculateCoursesInUnit(p.getId(), courses, postings);


        return OfficerDetailResponseDto.builder()
                .armyNo(p.getArmyNo())
                .rank(p.getRank())
                .fullName(p.getFullName())
                .gender(p.getGender())
                .dateOfBirth(p.getDateOfBirth())
                .dateOfCommission(p.getDateOfCommission())
                .dateOfSeniority(p.getDateOfSeniority())
                .coursesDone(coursesDone)
                .trainingYr(trainingYr)
                .coursesInUnit(coursesInUnit)
                .build();
    }

    private int calculateCoursesInUnit(
            Long personnelId,
            List<CourseDetails> courses,
            List<PostingDetails> postings) {

        if (postings.isEmpty()) {
            return 0;
        }

        // Get the latest posting in this unit
        PostingDetails latestPosting = postings.stream()
                .max(Comparator.comparing(PostingDetails::getFromDate))
                .orElse(null);

        if (latestPosting == null) {
            return 0;
        }

        LocalDate fromDate = latestPosting.getFromDate();
        LocalDate toDate = latestPosting.getToDate() != null ?
                latestPosting.getToDate() : LocalDate.now();

        // Count courses during this posting period
        LocalDate finalToDate = toDate;
        return (int) courses.stream()
                .filter(c -> c.getFromDate() != null &&
                        !c.getFromDate().isBefore(fromDate) &&
                        !c.getFromDate().isAfter(finalToDate))
                .count();
    }

}
