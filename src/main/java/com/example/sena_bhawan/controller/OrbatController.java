package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.OrbatCreateRequest;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.service.OrbatService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orbat")
@RequiredArgsConstructor
public class OrbatController {

    private final OrbatService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody OrbatCreateRequest request) {
        return ResponseEntity.ok(service.createFormation(request));
    }

    @GetMapping("/dropdown/{type}")
    public ResponseEntity<?> dropdown(
            @PathVariable String type,
            @RequestParam(required = false) Long parentId) {

        return ResponseEntity.ok(service.getDropdown(type, parentId));
    }

    @GetMapping("/tree")
    public ResponseEntity<?> getTree() {
        return ResponseEntity.ok(service.getOrbatTree());
    }

    // ✔ API 1: Only return SOS numbers
    @GetMapping("/sosnumbers")
    public List<String> getSosNumbers() {
        return service.getDistinctSosNumbers();
    }

    // ✔ API 2: Return SOS + Unit name (optional)
    @GetMapping("/soslist")
    public List<OrbatStructure> getSosList() {
        return service.getSosList();
    }


    // ================= ORBAT NAME DROPDOWNS =================
    @GetMapping("/dropdown/command")
    public ResponseEntity<List<String>> getCommandDropdown() {
        return ResponseEntity.ok(service.getCommandDropdown());
    }

    @GetMapping("/dropdown/corps")
    public ResponseEntity<List<String>> getCorpsDropdown() {
        return ResponseEntity.ok(service.getCorpsDropdown());
    }

    @GetMapping("/dropdown/division")
    public ResponseEntity<List<String>> getDivisionDropdown() {
        return ResponseEntity.ok(service.getDivisionDropdown());
    }

    // ================= FILTER DROPDOWNS =================
    @GetMapping("/filter/rank")
    public ResponseEntity<List<String>> getRankDropdown() {
        return ResponseEntity.ok(service.getRankDropdown());
    }

    @GetMapping("/filter/medical-category")
    public ResponseEntity<List<String>> getMedicalCategoryDropdown() {
        return ResponseEntity.ok(service.getMedicalCategoryDropdown());
    }

    @GetMapping("/filter/establishment-type")
    public ResponseEntity<List<String>> getEstablishmentTypeDropdown() {
        return ResponseEntity.ok(service.getEstablishmentTypeDropdown());
    }

    @GetMapping("/filter/area-type")
    public ResponseEntity<List<String>> getAreaTypeDropdown() {
        return ResponseEntity.ok(service.getAreaTypeDropdown());
    }

    @GetMapping("/filter/civil-qualification")
    public ResponseEntity<List<String>> getCivilQualificationDropdown() {
        return ResponseEntity.ok(service.getCivilQualificationDropdown());
    }

    @GetMapping("/filter/sports")
    public ResponseEntity<List<String>> getSportsDropdown() {
        return ResponseEntity.ok(service.getSportsDropdown());
    }

    @GetMapping("/filter/posting-due-months")
    public ResponseEntity<List<Integer>> getPostingDueMonthsDropdown() {
        return ResponseEntity.ok(service.getPostingDueMonthsDropdown());
    }
}
