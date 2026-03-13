package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.UnitSummaryRequestDto;
import com.example.sena_bhawan.dto.UnitSummaryResponseDto;
import com.example.sena_bhawan.service.UnitSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/unit-summary")
public class UnitSummaryController {

    @Autowired
    private UnitSummaryService unitSummaryService;

    @GetMapping
    public ResponseEntity<?> getUnitSummary(
            @RequestParam String formationType,
            @RequestParam String unitName) {

        try {
            // Validate input
            if (formationType == null || formationType.trim().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Formation type is required");
            }
            if (unitName == null || unitName.trim().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Unit name is required");
            }

            UnitSummaryRequestDto requestDto = new UnitSummaryRequestDto();
            requestDto.setFormationType(formationType.trim());
            requestDto.setUnitName(unitName.trim());

            UnitSummaryResponseDto response = unitSummaryService.getUnitSummary(requestDto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to fetch unit summary: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> getUnitSummaryPost(@RequestBody UnitSummaryRequestDto requestDto) {

        try {
            // Validate input
            if (requestDto.getFormationType() == null || requestDto.getFormationType().trim().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Formation type is required");
            }
            if (requestDto.getUnitName() == null || requestDto.getUnitName().trim().isEmpty()) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Unit name is required");
            }

            UnitSummaryResponseDto response = unitSummaryService.getUnitSummary(requestDto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to fetch unit summary: " + e.getMessage());
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
}
