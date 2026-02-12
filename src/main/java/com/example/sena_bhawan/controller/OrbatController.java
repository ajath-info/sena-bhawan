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
}
