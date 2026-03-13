package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CourseDetailsRequestDTO;
import com.example.sena_bhawan.dto.PersonnelFullDetailsDTO;
import com.example.sena_bhawan.dto.PostingDetailsDTO;
import com.example.sena_bhawan.dto.PostingRequestDTO;
import com.example.sena_bhawan.service.CourseDetailsService;
import com.example.sena_bhawan.service.PostingDetailsService;
import com.example.sena_bhawan.entity.PostingDetails;
import com.example.sena_bhawan.entity.CourseDetails;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/personnel-full-details")
@RequiredArgsConstructor
@Slf4j
public class PersonnelFullDetailsController {

    private final PostingDetailsService postingService;
    private final CourseDetailsService courseService;

    @PostMapping("/save")
    public ResponseEntity<?> savePosting(@RequestBody PostingRequestDTO dto) {
        try {
            PostingDetails saved = postingService.upsertPosting(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", getSuccessMessage(dto));
            response.put("data", saved);
            response.put("status", saved.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String getSuccessMessage(PostingRequestDTO dto) {
        if (dto.getTosUpdatedDate() != null) {
            return "New posting created successfully. Previous posting completed.";
        } else if (dto.getPostingId() != null) {
            return "Posting details updated successfully.";
        } else {
            return "Posting details saved successfully.";
        }
    }

    @DeleteMapping("/{postingId}")
    public ResponseEntity<?> deletePosting(@PathVariable Long postingId) {
        try {
            log.info("Delete request received for posting ID: {}", postingId);

            postingService.deletePosting(postingId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Posting deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting posting: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{personnelId}")
    public PersonnelFullDetailsDTO getAllDetails(@PathVariable Long personnelId) {

        List<PostingDetails> postingList = postingService.getByPersonnel(personnelId);

        PersonnelFullDetailsDTO response = new PersonnelFullDetailsDTO();
        response.setPersonnelId(personnelId);

        response.setPostingDetails(
                postingList.stream().map(pd -> {
                    PostingDetailsDTO dto = new PostingDetailsDTO();
                    dto.setPostingId(pd.getPostingId());
                    dto.setUnitName(pd.getUnitName());
                    dto.setLocation(pd.getLocation());
                    dto.setCommand(pd.getCommand());
                    dto.setAppointment(pd.getAppointment());
                    dto.setFromDate(pd.getFromDate());
                    dto.setToDate(pd.getToDate());
                    dto.setRemarks(pd.getRemarks());
                    dto.setDocumentPath(pd.getDocumentPath());
                    dto.setDuration(pd.getDuration());
                    return dto;
                }).toList()
        );

        return response;
    }

}
