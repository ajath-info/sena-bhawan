package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.AttendanceRequest;
import com.example.sena_bhawan.dto.AttendanceResponse;
import com.example.sena_bhawan.dto.PanelOfficerDTO;
import com.example.sena_bhawan.service.PanelSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panel")
@RequiredArgsConstructor
public class PanelSelectionController {

    private final PanelSelectionService panelSelectionService;

//    @GetMapping("/officers")
//    public List<PanelOfficerDTO> getPanelOfficers() {
//        return panelSelectionService.getEligibleOfficers();
//    }

//    @GetMapping("/officers")
//    public Page<PanelOfficerDTO> getOfficers(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ) {
//        return panelSelectionService.getOfficers(page, size);
//    }

    @GetMapping("/officers")
    public List<PanelOfficerDTO> getOfficers() {
        return panelSelectionService.getOfficers();
    }


    @GetMapping("/attendance/all")
    public List<AttendanceResponse> getAttendance(
            @RequestParam Long courseId,
            @RequestParam Long scheduleId
    ) {
        return panelSelectionService.getAttendance(courseId, scheduleId);
    }

    @PutMapping("/attendance/bulk")
    public void updateAttendanceBulk(
            @RequestBody List<AttendanceRequest> requests
    ) {
        panelSelectionService.updateAttendanceBulk(requests);
    }



    @PostMapping("/attendance")
    public void updateAttendance(
            @RequestBody List<AttendanceRequest> requests
    ) {
        for (AttendanceRequest req : requests) {
            panelSelectionService.updateAttendance(
                    req.getCourseId(),
                    req.getScheduleId(),
                    req.getPersonnelId(),
                    req.getStatus()
            );
        }
    }
    }


