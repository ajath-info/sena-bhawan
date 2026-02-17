package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.service.MasterDropdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/filter")
@RequiredArgsConstructor
public class MasterDropdownController {

    private final MasterDropdownService service;

    // ================= MEDICAL CATEGORY =================
    @GetMapping("/medical-category")
    public ResponseEntity<List<String>> getMedicalCategoryDropdown() {
        return ResponseEntity.ok(service.getMedicalCategoryDropdown());
    }

    // ================= ESTABLISHMENT TYPE =================
    @GetMapping("/establishment-type")
    public ResponseEntity<List<String>> getEstablishmentTypeDropdown() {
        return ResponseEntity.ok(service.getEstablishmentTypeDropdown());
    }

    // ================= AREA TYPE =================
    @GetMapping("/area-type")
    public ResponseEntity<List<String>> getAreaTypeDropdown() {
        return ResponseEntity.ok(service.getAreaTypeDropdown());
    }

    // ================= CIVIL QUALIFICATION =================
    @GetMapping("/civil-qualification")
    public ResponseEntity<List<String>> getCivilQualificationDropdown() {
        return ResponseEntity.ok(service.getCivilQualificationDropdown());
    }

    // ================= SPORTS =================
    @GetMapping("/sports")
    public ResponseEntity<List<String>> getSportsDropdown() {
        return ResponseEntity.ok(service.getSportsDropdown());
    }

    // ================= POSTING DUE MONTHS =================
    @GetMapping("/posting-due-months")
    public ResponseEntity<List<Integer>> getPostingDueMonthsDropdown() {
        return ResponseEntity.ok(service.getPostingDueMonthsDropdown());
    }
}
