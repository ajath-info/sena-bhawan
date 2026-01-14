package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.Officer;
import com.example.sena_bhawan.service.OfficerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/officers")
public class OfficerController {

    private final OfficerService service;

    public OfficerController(OfficerService service) {
        this.service = service;
    }

    // 1️⃣ Get all officers
    @GetMapping
    public List<Officer> getAllOfficers() {
        return service.getAllOfficers();
    }

    // 2️⃣ Get officer by ID
    @GetMapping("/{id}")
    public Officer getOfficerById(@PathVariable Integer id) {
        return service.getOfficerById(id);
    }

    // 3️⃣ Count of officers
    @GetMapping("/count")
    public long getOfficerCount() {
        return service.getOfficerCount();
    }

    // 4️⃣ Officers by status
    @GetMapping("/status/{status}")
    public List<Officer> getByStatus(@PathVariable String status) {
        return service.getOfficersByStatus(status);
    }

    // 5️⃣ Officers by rank
    @GetMapping("/rank/{rank}")
    public List<Officer> getByRank(@PathVariable String rank) {
        return service.getOfficersByRank(rank);
    }

    // 6️⃣ Officers by unit
    @GetMapping("/unit/{unit}")
    public List<Officer> getByUnit(@PathVariable String unit) {
        return service.getOfficersByUnit(unit);
    }
}
