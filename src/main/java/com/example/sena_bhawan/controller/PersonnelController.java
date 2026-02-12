package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.service.PersonnelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    @GetMapping
    public List<Personnel> getAll() {
        return personnelService.getallPersonnels();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPersonnelById(@PathVariable Long id) {
        Personnel personnel = personnelService.getPersonnelById(id);

        if (personnel == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Personnel not found with ID: " + id);
            return ResponseEntity.status(404).body(error);
        }

        return ResponseEntity.ok(personnel);
    }


    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<?> addPersonnel(
            @RequestPart("data") CreatePersonnelRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        Long id = personnelService.createPersonnel(request, image);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Personnel created successfully");
        response.put("id", id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/decorations")
    public void updateDecorations(
            @PathVariable Long id,
            @RequestBody List<DecorationRequest> decorations
    ) {
        personnelService.updateDecorations(id, decorations);
    }

    @PutMapping("/{id}/qualifications")
    public void updateQualifications(
            @PathVariable Long id,
            @RequestBody List<QualificationRequest> req
    ) {
        personnelService.updateQualifications(id, req);
    }

    @PutMapping("/{id}/additional-qualifications")
    public void updateAdditionalQualifications(
            @PathVariable Long id,
            @RequestBody List<AdditionalQualificationRequest> req
    ) {
        personnelService.updateAdditionalQualifications(id, req);
    }

    @PutMapping("/{id}/family")
    public void updateFamily(
            @PathVariable Long id,
            @RequestBody List<FamilyRequest> req
    ) {
        personnelService.updateFamily(id, req);
    }

    @PutMapping("/{id}/medical")
    public void updateMedical(
            @PathVariable Long id,
            @RequestBody MedicalUpdateRequest req
    ) {
        personnelService.updateMedical(id, req);
    }

    @PutMapping("/personnel/{id}/basic")
    public void updateBasicDetails(
            @PathVariable Long id,
            @RequestBody UpdatePersonnelRequest req
    ) {
        personnelService.updateBasicDetails(id, req);
    }

    @GetMapping("/officer-strength")  // This makes it /api/dashboard/officer-strength
    public ResponseEntity<RankStrengthResponse> getOfficerStrength() {
        RankStrengthResponse response = personnelService.getOfficerStrengthByRank();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/age-distribution")
    public ResponseEntity<AgeBandResponse> getAgeDistribution() {
        AgeBandResponse response = personnelService.getAgeBandDistribution();
        return ResponseEntity.ok(response);
    }

}
