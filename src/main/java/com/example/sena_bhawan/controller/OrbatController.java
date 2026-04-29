package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.service.FormationService;
import com.example.sena_bhawan.service.OrbatService;
import com.example.sena_bhawan.service.OrbatStructureService;
import com.example.sena_bhawan.service.impl.FormationServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orbat")
@RequiredArgsConstructor
public class OrbatController {

    private final OrbatService service;
    private final OrbatStructureService orbatStructureService;

    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getPaginatedOrbatStructures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String search) {

        Page<OrbatStructure> pageData = orbatStructureService.findPaginatedWithSearch(page, size, search);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageData.getContent());
        response.put("pageNumber", pageData.getNumber());
        response.put("pageSize", pageData.getSize());
        response.put("totalElements", pageData.getTotalElements());
        response.put("totalPages", pageData.getTotalPages());
        response.put("last", pageData.isLast());
        response.put("first", pageData.isFirst());
        response.put("empty", pageData.isEmpty());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUnits(@RequestParam String term) {
        try {
            List<OrbatSearchDTO> units = orbatStructureService.searchUnits(term);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", units);
            response.put("count", units.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "An error occurred while searching")
            );
        }
    }

//    @PostMapping("/create")
//    public ResponseEntity<?> create(@RequestBody OrbatCreateRequest request) {
//        return ResponseEntity.ok(service.createFormation(request));
//    }





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
