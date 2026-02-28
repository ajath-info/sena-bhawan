package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.OfficerListRequestDto;
import com.example.sena_bhawan.dto.OfficerListResponseDto;
import com.example.sena_bhawan.entity.Officer;
import com.example.sena_bhawan.service.OfficerDetailsService;
import com.example.sena_bhawan.service.OfficerService;
import com.example.sena_bhawan.service.impl.OfficerListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/officers")
public class OfficerController {

    private final OfficerService service;

    @Autowired
    private OfficerDetailsService officerDetailsService;

    @GetMapping("/by-formation-unit")
    public ResponseEntity<?> getOfficersByFormationAndUnit(
            @RequestParam(required = false) String formationType,
            @RequestParam(required = false) String unitName) {

        try {
            // Validate parameters
            if (formationType == null || formationType.trim().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Formation type is required");
            }
            if (unitName == null || unitName.trim().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Unit name is required");
            }

            OfficerListRequestDto requestDto = new OfficerListRequestDto();
            requestDto.setFormationType(formationType.trim());
            requestDto.setUnitName(unitName.trim());

            OfficerListResponseDto response = officerDetailsService
                    .getOfficersByFormationAndUnit(requestDto);

            if (response.getOfficers() == null || response.getOfficers().isEmpty()) {
                return errorResponse(HttpStatus.NOT_FOUND,
                        "No officers found for formation: " + formationType + ", unit: " + unitName);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching officer details: " + e.getMessage());
        }
    }

    @PostMapping("/by-formation-unit")
    public ResponseEntity<?> getOfficersByFormationAndUnitPost(
            @RequestBody OfficerListRequestDto requestDto) {

        try {
            // Validate request
            if (requestDto.getFormationType() == null || requestDto.getFormationType().trim().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Formation type is required");
            }
            if (requestDto.getUnitName() == null || requestDto.getUnitName().trim().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Unit name is required");
            }

            OfficerListResponseDto response = officerDetailsService
                    .getOfficersByFormationAndUnit(requestDto);

            if (response.getOfficers() == null || response.getOfficers().isEmpty()) {
                return errorResponse(HttpStatus.NOT_FOUND,
                        "No officers found for given criteria");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching officer details: " + e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return new ResponseEntity<>(error, status);
    }

    public OfficerController(OfficerService service) {
        this.service = service;
    }

    // 1️⃣ Get all officers
    @GetMapping
    public List<Officer> getAllOfficers() {
        return service.getAllOfficers();
    }

    // 2️⃣ Get officer by ID
    @GetMapping("/{id}")
    public Officer getOfficerById(@PathVariable Integer id) {
        return service.getOfficerById(id);
    }

    // 3️⃣ Count of officers
    @GetMapping("/count")
    public long getOfficerCount() {
        return service.getOfficerCount();
    }

    // 4️⃣ Officers by status
    @GetMapping("/status/{status}")
    public List<Officer> getByStatus(@PathVariable String status) {
        return service.getOfficersByStatus(status);
    }

    // 5️⃣ Officers by rank
    @GetMapping("/rank/{rank}")
    public List<Officer> getByRank(@PathVariable String rank) {
        return service.getOfficersByRank(rank);
    }

    // 6️⃣ Officers by unit
    @GetMapping("/unit/{unit}")
    public List<Officer> getByUnit(@PathVariable String unit) {
        return service.getOfficersByUnit(unit);
    }
}
