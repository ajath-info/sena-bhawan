package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.PersonnelListDTO;
import com.example.sena_bhawan.service.PersonnelAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PersonnelAttendanceController {

    private final PersonnelAttendanceService attendanceService;

    @GetMapping("/attendance/{attendanceStatus}")
    public ResponseEntity<Page<PersonnelListDTO>> getPersonnelByAttendanceStatus(
            @PathVariable String attendanceStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PersonnelListDTO> response = attendanceService.getPersonnelByAttendanceStatus(attendanceStatus, page, size);
        return ResponseEntity.ok(response);
    }
}