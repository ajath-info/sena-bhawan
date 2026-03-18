package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.EstablishmentRequest;
import com.example.sena_bhawan.dto.EstablishmentResponse;
import com.example.sena_bhawan.entity.FormationEstablishment;
import com.example.sena_bhawan.service.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/establishment")
@CrossOrigin("*")
public class EstablishmentController {

    @Autowired
    private EstablishmentService service;

    @GetMapping
    public ResponseEntity<?> getAllEstablishment() {
        return ResponseEntity.ok(
                service.getEstablishmentName()
        );
    }

    // 🔹 Get data for a unit - type parameter is now a query param
    @GetMapping("/{orbatId}")
    public ResponseEntity<?> getByOrbatId(
            @PathVariable Long orbatId,
            @RequestParam(required = false) FormationEstablishment.EstablishmentType type) {

        return ResponseEntity.ok(
                service.getByOrbatId(orbatId, type)
        );
    }

    // 🔹 Update button click - type in path is removed, now in request body
    @PutMapping("/{orbatId}")
    public ResponseEntity<String> updateEstablishment(
            @PathVariable Long orbatId,
            @RequestBody EstablishmentRequest request) {

        return ResponseEntity.ok(
                service.updateEstablishment(orbatId, request)
        );
    }
}