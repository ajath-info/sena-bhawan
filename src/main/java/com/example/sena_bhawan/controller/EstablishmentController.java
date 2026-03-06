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

    // 🔹 Get data when PE/WE selected
    @GetMapping("/{orbatId}/{type}")
    public ResponseEntity<?> getByOrbatAndType(
            @PathVariable Long orbatId,
            @PathVariable FormationEstablishment.EstablishmentType type) {

        return ResponseEntity.ok(
                service.getByOrbatAndType(orbatId, type)
        );
    }

    // 🔹 Update button click
    @PutMapping("/{orbatId}/{type}")
    public ResponseEntity<String> updateEstablishment(
            @PathVariable Long orbatId,
            @PathVariable FormationEstablishment.EstablishmentType type,
            @RequestBody EstablishmentRequest request) {

        return ResponseEntity.ok(
                service.updateEstablishment(orbatId, type, request)
        );
    }
}
