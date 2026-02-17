package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.OrbatSimpleDTO;
import com.example.sena_bhawan.service.OrbatStructureService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orbat")
public class OrbatStructureController {

    private final OrbatStructureService service;

    public OrbatStructureController(OrbatStructureService service) {
        this.service = service;
    }

    // 1️⃣ Get all Commands
    @GetMapping("/commands")
    public List<OrbatSimpleDTO> getAllCommands() {
        return service.getAllCommands();
    }

    // 2️⃣ Get all Corps
    @GetMapping("/corps")
    public List<OrbatSimpleDTO> getAllCorps() {
        return service.getAllCorps();
    }


    // 3️⃣ Get Corps by Command
    @GetMapping("/corps/by-command-name/{commandName}")
    public List<OrbatSimpleDTO> getCorpsByCommandName(
            @PathVariable String commandName) {
        return service.getCorpsByCommand(commandName);
    }

    @GetMapping("/dropdown/command")
    public ResponseEntity<List<String>> getCommandDropdown() {
        return ResponseEntity.ok(service.getCommandDropdown());
    }

    @GetMapping("/dropdown/corps")
    public ResponseEntity<List<String>> getCorpsDropdown() {
        return ResponseEntity.ok(service.getCorpsDropdown());
    }

    @GetMapping("/dropdown/division")
    public ResponseEntity<List<String>> getDivisionDropdown() {
        return ResponseEntity.ok(service.getDivisionDropdown());
    }
}
