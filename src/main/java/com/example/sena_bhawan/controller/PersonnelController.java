package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.*;

import com.example.sena_bhawan.entity.CourseMaster;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.entity.PostingDetails;
import com.example.sena_bhawan.service.PersonnelService;
import com.example.sena_bhawan.service.PostingDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;
    private final PostingDetailsService postingService;


    public PersonnelController(PersonnelService personnelService, PostingDetailsService postingService) {
        this.personnelService = personnelService;
        this.postingService = postingService;
    }

    @GetMapping
    public List<Personnel> getAll() {
        return personnelService.getallPersonnels();
    }

    @GetMapping("/{personnelId}/posting-history")
    public ResponseEntity<?> getPostingHistory(@PathVariable Long personnelId) {
        try {
            List<PostingHistoryDTO> history = postingService.getPostingHistory(personnelId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            response.put("count", history.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    @DeleteMapping("/cancel-under-posting")
    public ResponseEntity<?> cancelUnderPosting(@RequestParam Long personnelId) {
        try {
            PostingDetails restored = postingService.cancelUnderPostingByPersonnelId(personnelId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Current UNDER_POSTING cancelled. Previous POSTED restored.");
            response.put("data", restored);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    @GetMapping("/{personnelId}/current-posting")
    public ResponseEntity<?> getCurrentPosting(@PathVariable Long personnelId) {

        PostingResponseDTO dto = postingService.getCurrentPostingDetails(personnelId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", dto);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchPersonnels(@RequestParam String term) {
        try {
            List<PersonnelDTO> units = personnelService.searchPersonnels(term);

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
            @RequestPart(value = "image", required = false) MultipartFile image
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

    @PutMapping("/{personnelId}/sports")
    public ResponseEntity<String> updateSports(
            @PathVariable Long personnelId,
            @RequestBody List<SportsRequest> requestList) {

        personnelService.updateSports(personnelId, requestList);

        return ResponseEntity.ok("Sports updated successfully");
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

//    @GetMapping("/medical-distribution")
//    public ResponseEntity<MedicalCategoryResponse> getMedicalDistribution() {
//        MedicalCategoryResponse response = personnelService.getMedicalCategoryDistribution();
//        return ResponseEntity.ok(response);
//    }


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

//    @PutMapping("/{id}/medical")
//    public ResponseEntity<?> updateMedical(
//            @PathVariable Long id,
//            @RequestBody UpdateMedicalRequest req) {
//
//        personnelService.updateMedical(id, req);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "Medical updated successfully");
//
//        return ResponseEntity.ok(response);
//    }

    // ================= CHILD SECTION UPDATES =================
//
//    @PutMapping("/{id}/decorations")
//    public ResponseEntity<?> updateDecorations(
//            @PathVariable Long id,
//            @RequestBody List<CreatePersonnelRequest.DecorationDTO> list) {
//
//        personnelService.updateDecorations(id, list);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "Decorations updated successfully");
//
//        return ResponseEntity.ok(response);
//    }

//    @PutMapping("/{id}/qualifications")
//    public ResponseEntity<?> updateQualifications(
//            @PathVariable Long id,
//            @RequestBody List<CreatePersonnelRequest.QualificationDTO> list) {
//
//        personnelService.updateQualifications(id, list);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "Qualifications updated successfully");
//
//        return ResponseEntity.ok(response);
//    }

//    @PutMapping("/{id}/additional-qualifications")
//    public ResponseEntity<?> updateAdditionalQualifications(
//            @PathVariable Long id,
//            @RequestBody List<CreatePersonnelRequest.AdditionalQualificationDTO> list) {
//
//        personnelService.updateAdditionalQualifications(id, list);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "Additional qualifications updated successfully");
//
//        return ResponseEntity.ok(response);
//    }

//    @PutMapping("/{id}/family")
//    public ResponseEntity<?> updateFamily(
//            @PathVariable Long id,
//            @RequestBody List<CreatePersonnelRequest.FamilyDTO> list) {
//
//        personnelService.updateFamily(id, list);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "Family updated successfully");
//
//        return ResponseEntity.ok(response);
//    }

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

    @PostMapping("/filter")
    public ResponseEntity<?> filterPersonnel(@RequestBody PersonnelFilterRequest filter, @RequestParam(defaultValue = "0") int cpg) {
        if (cpg == 1) {
            // Return direct list when cpg=1
            List<PersonnelListDTO> result = personnelService.filterAllPersonnel(filter);
            return ResponseEntity.ok(result);
        } else {
            // Return paginated response when cpg=0
            Page<PersonnelListDTO> result = personnelService.filterPersonnel(filter);
            return ResponseEntity.ok(result);
        }
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<PersonnelListDTO> getPersonnelById(@PathVariable Long id) {
//        PersonnelFilterRequest filter = new PersonnelFilterRequest();
//        filter.page = 0;
//        filter.size = 1;
//        Page<PersonnelListDTO> result = personnelService.filterPersonnel(filter);
//
//        if (result.hasContent()) {
//            return ResponseEntity.ok(result.getContent().get(0));
//        }
//        return ResponseEntity.notFound().build();
//    }


    @GetMapping("/unit/{unitId}/summary")
    public ResponseEntity<?> getSummary(@PathVariable Long unitId) {
        return ResponseEntity.ok(
                Map.of("status", "success",
                        "data", personnelService.getOfficerSummaryByUnit(unitId))
        );
    }

    @GetMapping("/unit/{unitId}/table")
    public ResponseEntity<?> getTable(@PathVariable Long unitId) {
        return ResponseEntity.ok(
                Map.of("status", "success",
                        "data", personnelService.getOfficerTableByUnit(unitId))
        );
    }

    @GetMapping("/retirement-forecast")
    public ResponseEntity<RetirementForecastResponse> getRetirementForecast() {
        RetirementForecastResponse response = personnelService.getRetirementForecast();
        return ResponseEntity.ok(response);
    }

}
