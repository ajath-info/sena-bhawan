package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.ModuleEntity;
import com.example.sena_bhawan.service.ModuleService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin("*")
public class ModuleController {

    private final ModuleService service;

    public ModuleController(ModuleService service) {
        this.service = service;
    }

    @GetMapping
    public List<ModuleEntity> getAllModules() {
        return service.getAllModules();
    }

    @GetMapping("/{id}")
    public ModuleEntity getModuleById(@PathVariable Long id) {
        return service.getModuleById(id);
    }

    @PostMapping
    public ModuleEntity addModule(@RequestBody ModuleEntity module) {
        return service.addModule(module);
    }

    @PutMapping("/{id}")
    public ModuleEntity updateModule(@PathVariable Long id, @RequestBody ModuleEntity module) {
        return service.updateModule(id, module);
    }

    @DeleteMapping("/{id}")
    public String deleteModule(@PathVariable Long id) {
        service.deleteModule(id);
        return "Module deleted successfully!";
    }
}
