package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.OfficerPostingRequestDTO;
import com.example.sena_bhawan.entity.OfficerPosting;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.service.OfficerPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/officer-postings")
@RequiredArgsConstructor
public class OfficerPostingController {

    private final OfficerPostingService officerPostingService;

    @PostMapping
    public ResponseEntity<OfficerPosting> createPosting(
            @RequestBody OfficerPostingRequestDTO dto) {

        return ResponseEntity.ok(officerPostingService.createPosting(dto));
    }


    @PostMapping("/with-personnel")
    public ResponseEntity<?> createPosting(
            @RequestParam Long personnelId,
            @RequestBody OfficerPosting posting
    ) {
        posting.setPersonnel(new Personnel(personnelId));
        return ResponseEntity.ok(officerPostingService.createPosting(posting));
    }

}
