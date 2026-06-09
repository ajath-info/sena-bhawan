package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.PersonnelFilterDataRequest;
import com.example.sena_bhawan.dto.PersonnelListDTO;
import com.example.sena_bhawan.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonnelFilterService {

    private final PersonnelRepository personnelRepository;

    public Page<PersonnelListDTO> filterPersonnel(PersonnelFilterDataRequest request) {
        // Calculate age band dates
        LocalDate now = LocalDate.now();
        LocalDate date30 = now.minusYears(30);
        LocalDate date35 = now.minusYears(35);
        LocalDate date40 = now.minusYears(40);
        LocalDate date45 = now.minusYears(45);
        LocalDate date50 = now.minusYears(50);

        // Set default pagination values
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        int offset = page * size;

        // Execute query
        List<Object[]> results = personnelRepository.findFilteredPersonnelWithDetails(
                request.getMedicalCode(),
                request.getRank(),
                request.getAgeBand(),
                date30, date35, date40, date45, date50,
                request.getRetirementYear(),
                offset,
                size
        );

        // Count total records
        long total = personnelRepository.countFilteredPersonnel(
                request.getMedicalCode(),
                request.getRank(),
                request.getAgeBand(),
                date30, date35, date40, date45, date50,
                request.getRetirementYear()
        );

        // Convert results to DTOs
        List<PersonnelListDTO> personnelList = results.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Calculate additional fields for each personnel
        personnelList.forEach(this::calculateAdditionalFields);

        return new PageImpl<>(personnelList, PageRequest.of(page, size), total);
    }

    private PersonnelListDTO convertToDTO(Object[] row) {
        PersonnelListDTO dto = new PersonnelListDTO();

        dto.setId(getLongValue(row[0]));
        dto.setArmyNo(getStringValue(row[1]));
        dto.setRank(getStringValue(row[2]));
        dto.setFullName(getStringValue(row[3]));
        dto.setDateOfBirth(getStringValue(row[4]));
        dto.setDateOfCommission(getStringValue(row[5]));
        dto.setDateOfSeniority(getStringValue(row[6]));
        dto.setMedicalCode(getStringValue(row[7]));
        dto.setReligion(getStringValue(row[8]));
        dto.setMaritalStatus(getStringValue(row[9]));
        dto.setMobileNumber(getStringValue(row[10]));
        dto.setEmailAddress(getStringValue(row[11]));
        dto.setCity(getStringValue(row[12]));
        dto.setState(getStringValue(row[13]));
        dto.setPlaceOfBirth(getStringValue(row[14]));

        // Handle unit JSON object
        Object unitObj = row[15];
        if (unitObj != null) {
            String unitStr = unitObj.toString();
            dto.setUnit(extractUnitName(unitStr));
            dto.setAreaType(extractAreaType(unitStr));
        } else {
            dto.setUnit("-");
            dto.setAreaType("-");
        }

        dto.setDivision(getStringValue(row[16]));
        dto.setEstablishmentType(getStringValue(row[17]));
        dto.setCommand(getStringValue(row[18]));
        dto.setCorps(getStringValue(row[19]));
        dto.setCourse(getStringValue(row[20]));
        dto.setCivilQual(getStringValue(row[21]));
        dto.setSports(getStringValue(row[22]));
        dto.setTotalCoursesOverall(getIntValue(row[23]));
        dto.setTotalCoursesCurrentYear(getIntValue(row[24]));
        dto.setTotalCoursesCurrentUnit(getIntValue(row[25]));
        dto.setTotalCoursesDone(getIntValue(row[26]));
        dto.setCoursesTrainingYr(getIntValue(row[27]));
        dto.setCoursesInUnit(getIntValue(row[28]));
        dto.setTosDate(getStringValue(row[29]));

        return dto;
    }

    private void calculateAdditionalFields(PersonnelListDTO dto) {
        // Calculate Panel Status based on courses done
        Integer totalCourses = dto.getTotalCoursesDone();
        if (totalCourses == null) totalCourses = 0;

        if (totalCourses == 0) {
            dto.setPanelStatus("Not Started");
        } else if (totalCourses < 3) {
            dto.setPanelStatus("In Progress");
        } else {
            dto.setPanelStatus("Completed");
        }

        // Calculate Posting Due Months based on TOS date
        if (dto.getTosDate() != null && !"-".equals(dto.getTosDate())) {
            try {
                LocalDate tosDate = LocalDate.parse(dto.getTosDate());
                LocalDate now = LocalDate.now();
                long monthsBetween = java.time.temporal.ChronoUnit.MONTHS.between(tosDate, now);
                if (monthsBetween >= 24) {
                    dto.setPostingDueMonths("Overdue (" + monthsBetween + " months)");
                } else {
                    dto.setPostingDueMonths((24 - monthsBetween) + " months remaining");
                }
            } catch (Exception e) {
                dto.setPostingDueMonths("-");
            }
        } else {
            dto.setPostingDueMonths("-");
        }
    }

    // Helper methods for type conversion
    private String getStringValue(Object obj) {
        return obj != null ? obj.toString() : "-";
    }

    private Long getLongValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntValue(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Long) return ((Long) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String extractUnitName(String jsonStr) {
        try {
            if (jsonStr.contains("unit_name")) {
                int start = jsonStr.indexOf("unit_name") + 11;
                int end = jsonStr.indexOf(",", start);
                if (end == -1) end = jsonStr.indexOf("}", start);
                String value = jsonStr.substring(start, end).replace("\"", "").trim();
                return value.isEmpty() ? "-" : value;
            }
        } catch (Exception e) {
            log.error("Error extracting unit name from JSON: {}", jsonStr, e);
        }
        return "-";
    }

    private String extractAreaType(String jsonStr) {
        try {
            if (jsonStr.contains("area_type")) {
                int start = jsonStr.indexOf("area_type") + 11;
                int end = jsonStr.indexOf("}", start);
                String value = jsonStr.substring(start, end).replace("\"", "").trim();
                return value.isEmpty() ? "-" : value;
            }
        } catch (Exception e) {
            log.error("Error extracting area type from JSON: {}", jsonStr, e);
        }
        return "-";
    }
}