package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CreatePersonnelRequest;
import com.example.sena_bhawan.entity.CourseMaster;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.service.PersonnelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public ResponseEntity<?> addPersonnel(@RequestBody CreatePersonnelRequest request) {
        Long id = personnelService.createPersonnel(request);

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
}
