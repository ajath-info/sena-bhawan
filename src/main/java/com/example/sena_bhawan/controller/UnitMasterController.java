package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.UnitMaster;
import com.example.sena_bhawan.service.UnitMasterService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitMasterController {

    private final UnitMasterService service;

    public UnitMasterController(UnitMasterService service) {
        this.service = service;
    }

    @PostMapping
    public UnitMaster createUnit(@RequestBody UnitMaster unit) {
        return service.create(unit);
    }

    @GetMapping
    public List<UnitMaster> getAllUnits() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UnitMaster getUnitById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public UnitMaster updateUnit(@PathVariable Long id, @RequestBody UnitMaster unit) {
        return service.update(id, unit);
    }

    @DeleteMapping("/{id}")
    public String deleteUnit(@PathVariable Long id) {
        service.delete(id);
        return "Unit deleted successfully";
    }
}
