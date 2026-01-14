package com.example.sena_bhawan.controller;


import com.example.sena_bhawan.entity.PersonnelInformation;
import com.example.sena_bhawan.service.PersonnelInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/personnelinformation")
//@CrossOrigin(origins = "*")
public class PersonnelInformationController {

    private final PersonnelInformationService service;

    public PersonnelInformationController(
            PersonnelInformationService service) {
        this.service = service;
    }

    // 🔹 SAVE FORM DATA
    @PostMapping
    public ResponseEntity<PersonnelInformation> save(
            @RequestBody PersonnelInformation info) {
        return ResponseEntity.ok(service.save(info));
    }

    // 🔹 GET BY ARMY NO (personnel.id)
    @GetMapping("/{personnelId}")
    public ResponseEntity<List<PersonnelInformation>> getByPersonnelId(
            @PathVariable Long personnelId) {
        return ResponseEntity.ok(
                service.getByPersonnelId(personnelId));
    }
}



