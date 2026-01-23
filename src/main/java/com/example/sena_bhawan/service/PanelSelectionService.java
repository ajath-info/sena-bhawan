package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.AttendanceRequest;
import com.example.sena_bhawan.dto.AttendanceResponse;
import com.example.sena_bhawan.dto.PanelOfficerDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PanelSelectionService {

    List<PanelOfficerDTO> getEligibleOfficers();
//    Page<PanelOfficerDTO> getOfficers(int page, int size);
    List<PanelOfficerDTO> getOfficers();


    void updateAttendance(Integer courseId, Long scheduleId,
                          Long personnelId, String status);

    void updateAttendanceBulk(List<AttendanceRequest> requests);


    public List<AttendanceResponse> getAttendance(Long courseId, Long scheduleId);

}

