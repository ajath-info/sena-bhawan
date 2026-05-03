package com.example.sena_bhawan.controller;


import com.example.sena_bhawan.entity.PersonnelInformation;
import com.example.sena_bhawan.service.PersonnelInformationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody PersonnelInformation info) {

        Map<String, Object> response = new HashMap<>();

        try {
            PersonnelInformation updated = service.update(id, info);

            response.put("message", "Updated successfully");
            response.put("success", 1);
            response.put("data", updated);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("success", 0);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 🔹 GET BY ARMY NO (personnel.id)
    @GetMapping("/{personnelId}")
    public ResponseEntity<List<PersonnelInformation>> getByPersonnelId(
            @PathVariable Long personnelId) {
        return ResponseEntity.ok(
                service.getByPersonnelId(personnelId));
    }
}



