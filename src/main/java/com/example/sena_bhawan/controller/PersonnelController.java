package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.CourseMaster;
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


//    @PostMapping("/add")
//    public ResponseEntity<?> addPersonnel(@RequestBody CreatePersonnelRequest request) {
//        Long id = personnelService.createPersonnel(request);
//        return ResponseEntity.ok("Personnel created with ID: " + id);
//    }


    // ================= SECTION-WISE UPDATE =================

    @PutMapping("/{id}/basic-info")
    public ResponseEntity<?> updateBasicInfo(
            @PathVariable Long id,
            @RequestBody UpdateBasicInfoRequest req) {

        personnelService.updateBasicInfo(id, req);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Basic info updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/service")
    public ResponseEntity<?> updateService(
            @PathVariable Long id,
            @RequestBody UpdateServiceRequest req) {

        personnelService.updateServiceDetails(id, req);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Service details updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/address")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long id,
            @RequestBody UpdateAddressRequest req) {

        personnelService.updateAddress(id, req);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Address updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/contact")
    public ResponseEntity<?> updateContact(
            @PathVariable Long id,
            @RequestBody UpdateContactRequest req) {

        personnelService.updateContact(id, req);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Contact updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/medical")
    public ResponseEntity<?> updateMedical(
            @PathVariable Long id,
            @RequestBody UpdateMedicalRequest req) {

        personnelService.updateMedical(id, req);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Medical updated successfully");

        return ResponseEntity.ok(response);
    }

    // ================= CHILD SECTION UPDATES =================

    @PutMapping("/{id}/decorations")
    public ResponseEntity<?> updateDecorations(
            @PathVariable Long id,
            @RequestBody List<CreatePersonnelRequest.DecorationDTO> list) {

        personnelService.updateDecorations(id, list);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Decorations updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/qualifications")
    public ResponseEntity<?> updateQualifications(
            @PathVariable Long id,
            @RequestBody List<CreatePersonnelRequest.QualificationDTO> list) {

        personnelService.updateQualifications(id, list);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Qualifications updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/additional-qualifications")
    public ResponseEntity<?> updateAdditionalQualifications(
            @PathVariable Long id,
            @RequestBody List<CreatePersonnelRequest.AdditionalQualificationDTO> list) {

        personnelService.updateAdditionalQualifications(id, list);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Additional qualifications updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/family")
    public ResponseEntity<?> updateFamily(
            @PathVariable Long id,
            @RequestBody List<CreatePersonnelRequest.FamilyDTO> list) {

        personnelService.updateFamily(id, list);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Family updated successfully");

        return ResponseEntity.ok(response);
    }

    // ================= IMAGE UPDATE =================

    @PutMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<?> updateOfficerImage(
            @PathVariable Long id,
            @RequestPart("image") MultipartFile image) {

        personnelService.updateOfficerImage(id, image);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Officer image updated successfully");

        return ResponseEntity.ok(response);
    }
}
