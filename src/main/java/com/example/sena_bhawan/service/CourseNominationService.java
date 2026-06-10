package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CourseNominationDTO;
import com.example.sena_bhawan.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseNominationService {
    
    private final PersonnelRepository personnelRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Page<CourseNominationDTO> getNominationsByCourseName(String courseName, int page, int size) {
        int offset = page * size;
        
        List<Object[]> results = personnelRepository.findNominationsByCourseName(courseName, offset, size);
        long total = personnelRepository.countNominationsByCourseName(courseName);
        
        List<CourseNominationDTO> nominations = results.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(nominations, PageRequest.of(page, size), total);
    }
    
    private CourseNominationDTO convertToDTO(Object[] row) {
        CourseNominationDTO dto = new CourseNominationDTO();
        
        // Course Details
        dto.setCourseName(getStringValue(row[0]));
        dto.setCourseId(getIntValue(row[1]));
        dto.setCourseLocation(getStringValue(row[2]));
        dto.setCourseDuration(getIntValue(row[3]));
        
        // Schedule Details
        dto.setScheduleId(getLongValue(row[4]));
        dto.setBatchNumber(getStringValue(row[5]));
        dto.setYear(getStringValue(row[6]));
        dto.setStartDate(getStringValue(row[7]));
        dto.setEndDate(getStringValue(row[8]));
        dto.setVenue(getStringValue(row[9]));
        dto.setCourseStrength(getStringValue(row[10]));
        dto.setScheduleRemarks(getStringValue(row[11]));
        
        // Nomination Details
        dto.setNominationId(getLongValue(row[12]));
        dto.setNominationStatus(getStringValue(row[13]));
        dto.setAttendanceStatus(getStringValue(row[14]));
        dto.setGrade(getStringValue(row[15]));
        dto.setGradeStatus(getStringValue(row[16]));
        dto.setInstructorAward(getBooleanValue(row[17]));
        dto.setSerialNumber(getIntValue(row[18]));
        dto.setNominationCreatedAt(getStringValue(row[19]));
        dto.setNominationUpdatedAt(getStringValue(row[20]));
        
        // Personnel Details
        dto.setPersonnelId(getLongValue(row[21]));
        dto.setArmyNo(getStringValue(row[22]));
        dto.setRank(getStringValue(row[23]));
        dto.setFullName(getStringValue(row[24]));
        dto.setFirstName(getStringValue(row[25]));
        dto.setLastName(getStringValue(row[26]));
        dto.setGender(getStringValue(row[27]));
        dto.setCommissionType(getStringValue(row[28]));
        dto.setDateOfBirth(getStringValue(row[29]));
        dto.setDateOfCommission(getStringValue(row[30]));
        dto.setDateOfSeniority(getStringValue(row[31]));
        dto.setMedicalCode(getStringValue(row[32]));
        dto.setReligion(getStringValue(row[33]));
        dto.setMaritalStatus(getStringValue(row[34]));
        dto.setMobileNumber(getStringValue(row[35]));
        dto.setEmailAddress(getStringValue(row[36]));
        dto.setCity(getStringValue(row[37]));
        dto.setState(getStringValue(row[38]));
        dto.setDistrict(getStringValue(row[39]));
        
        // Current Posting Details (JSON object)
        Object unitObj = row[40];
        if (unitObj != null) {
            String unitStr = unitObj.toString();
            dto.setCurrentUnit(extractUnitName(unitStr));
            dto.setAreaType(extractAreaType(unitStr));
        } else {
            dto.setCurrentUnit("-");
            dto.setAreaType("-");
        }
        
        dto.setDivision(getStringValue(row[41]));
        dto.setCommand(getStringValue(row[42]));
        
        return dto;
    }
    
    // Helper methods
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
    
    private Boolean getBooleanValue(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean) return (Boolean) obj;
        return Boolean.parseBoolean(obj.toString());
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
            log.error("Error extracting unit name", e);
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
            log.error("Error extracting area type", e);
        }
        return "-";
    }
}