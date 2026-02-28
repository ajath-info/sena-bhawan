package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.OfficerDetailResponseDto;
import com.example.sena_bhawan.dto.OfficerListRequestDto;
import com.example.sena_bhawan.dto.OfficerListResponseDto;
import com.example.sena_bhawan.dto.OfficerSummaryResponseDto;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.repository.PostingDetailsRepository;
import com.example.sena_bhawan.service.OfficerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfficerListService {

    private final PostingDetailsRepository postingDetailsRepository;
    private final PersonnelRepository personnelRepository;

    public OfficerListResponseDto getOfficersByFormationAndUnit(OfficerListRequestDto requestDto) {
        OfficerListResponseDto response = null;

        try {
            // Get personnel IDs from posting_details
            List<Long> personnelIds = postingDetailsRepository
                    .findPersonnelIdsByFormationTypeAndUnitName(
                            requestDto.getFormationType(),
                            requestDto.getUnitName()
                    );

            if (personnelIds == null || personnelIds.isEmpty()) {
                return OfficerListResponseDto.builder()
                        .formationType(requestDto.getFormationType())
                        .unitName(requestDto.getUnitName())
                        .summary(null)
                        .officers(new ArrayList<>())
                        .build();
            }

            // Fetch personnel details
            List<Personnel> personnelList = personnelRepository.findByIdIn(personnelIds);

            // Calculate summary
            OfficerSummaryResponseDto summary = calculateSummary(personnelIds, personnelList);

            // Map to DTOs
            List<OfficerDetailResponseDto> officerDetails = personnelList.stream()
                    .map(this::mapToOfficerDetailDto)
                    .collect(Collectors.toList());

            // Build response
            response = OfficerListResponseDto.builder()
                    .formationType(requestDto.getFormationType())
                    .unitName(requestDto.getUnitName())
                    .summary(summary)
                    .officers(officerDetails)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching officer details: " + e.getMessage());
        }

        return response;
    }

    private OfficerSummaryResponseDto calculateSummary(List<Long> personnelIds, List<Personnel> personnelList) {
        OfficerSummaryResponseDto summary = null;

        try {
            int totalOfficers = personnelList.size();
            int totalCoursesDone = 0;
            int coursesTrainingYr = 0;

            for (Personnel p : personnelList) {
                if (p.getQualifications() != null) {
                    totalCoursesDone += p.getQualifications().size();
                    coursesTrainingYr += p.getQualifications().size();
                }
            }

            // Get date statistics
            LocalDate earliestSeniority = personnelRepository.findEarliestSeniority(personnelIds).orElse(null);
            LocalDate latestSeniority = personnelRepository.findLatestSeniority(personnelIds).orElse(null);
            LocalDate earliestCommission = personnelRepository.findEarliestCommission(personnelIds).orElse(null);
            LocalDate latestCommission = personnelRepository.findLatestCommission(personnelIds).orElse(null);

            summary = OfficerSummaryResponseDto.builder()
                    .totalOfficers(totalOfficers)
                    .earliestSeniority(earliestSeniority)
                    .latestSeniority(latestSeniority)
                    .totalCoursesDone(totalCoursesDone)
                    .earliestCommission(earliestCommission)
                    .latestCommission(latestCommission)
                    .coursesTrainingYr(coursesTrainingYr)
                    .coursesInUnit(personnelList.size())
                    .build();

        } catch (Exception e) {
            return null;
        }

        return summary;
    }

    private OfficerDetailResponseDto mapToOfficerDetailDto(Personnel personnel) {
        OfficerDetailResponseDto dto = null;

        try {
            String gender = "M";
            if (personnel.getFullName() != null) {
                String name = personnel.getFullName();
                if (name.contains("Kumari") || name.contains("Devi") || name.endsWith("a")) {
                    gender = "F";
                }
            }

            int coursesDone = personnel.getQualifications() != null ?
                    personnel.getQualifications().size() : 0;

            dto = OfficerDetailResponseDto.builder()
                    .armyNo(personnel.getArmyNo())
                    .rank(personnel.getRank())
                    .fullName(personnel.getFullName())
                    .gender(gender)
                    .dateOfBirth(personnel.getDateOfBirth())
                    .dateOfCommission(personnel.getDateOfCommission())
                    .dateOfSeniority(personnel.getDateOfSeniority())
                    .coursesDone(coursesDone)
                    .trainingYr(coursesDone)
                    .coursesInUnit(1)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error mapping personnel details");
        }

        return dto;
    }
}