package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.FormationRequestDTO;
import com.example.sena_bhawan.dto.OrbatCreateRequest;
import com.example.sena_bhawan.dto.OrbatDropdownDTO;
import com.example.sena_bhawan.dto.OrbatSimpleDTO;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.service.FormationService;
import com.example.sena_bhawan.service.OrbatService;
import com.example.sena_bhawan.service.OrbatStructureService;
import com.example.sena_bhawan.service.impl.FormationServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orbat")
@RequiredArgsConstructor
public class OrbatController {

    private final OrbatService service;
    private final OrbatStructureService orbatStructureService;
    @Autowired
    private FormationServiceImpl formationService;

//    @PostMapping("/create")
//    public ResponseEntity<?> create(@RequestBody OrbatCreateRequest request) {
//        return ResponseEntity.ok(service.createFormation(request));
//    }

    @GetMapping("/formation-type/{formationType}")
    public List<OrbatDropdownDTO> getByFormationType(@PathVariable String formationType) {
        return orbatStructureService.getByFormationType(formationType);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createFormation(@RequestBody FormationRequestDTO dto) {

        return ResponseEntity.ok(formationService.createFormation(dto));
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
    public List<String> getSusNumbers() {
        return service.getDistinctSusNumbers();
    }

    // ✔ API 2: Return SOS + Unit name (optional)
    @GetMapping("/soslist")
    public List<OrbatStructure> getSusList() {
        return service.getSusList();
    }

}
