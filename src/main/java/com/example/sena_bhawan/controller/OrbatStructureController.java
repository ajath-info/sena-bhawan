package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.DropdownDTO;
import com.example.sena_bhawan.dto.FormationRequestDTO;
import com.example.sena_bhawan.dto.OrbatDropdownDTO;
import com.example.sena_bhawan.dto.OrbatSimpleDTO;
import com.example.sena_bhawan.service.OrbatStructureService;

import com.example.sena_bhawan.service.impl.FormationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orbat")
public class OrbatStructureController {
    @Autowired
    private FormationServiceImpl formationService;

    private final OrbatStructureService service;

    public OrbatStructureController(OrbatStructureService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createFormation(@RequestBody FormationRequestDTO dto) {

        return ResponseEntity.ok(formationService.createFormation(dto));
    }

    @GetMapping("/formation-type/{formationType}")
    public List<OrbatDropdownDTO> getByFormationType(@PathVariable String formationType) {
        return service.getByFormationType(formationType);
    }

    @GetMapping("/hq")
    public List<DropdownDTO> getAllHq() {
        return service.getAllHq();
    }

    // 1️⃣ Get all Commands
    @GetMapping("/commands")
    public List<DropdownDTO> getAllCommands() {
        return service.getAllCommands();
    }

    // 2️⃣ Get all Corps
//    @GetMapping("/corps")
//    public List<DropdownDTO> getAllCorps() {
//        return service.getAllCorps();
//    }
//
//    @GetMapping("/divisions")
//    public List<DropdownDTO> getAllDivisions() {
//        return service.getAllDivisions();
//    }
//
//    @GetMapping("/brigades")
//    public List<DropdownDTO> getAllBrigades() {
//        return service.getAllBrigades();
//    }

    @GetMapping("/units")
    public List<DropdownDTO> getAllUnits() {
        return service.getAllUnits();
    }
    // Corps filtered by commandId (required for proper hierarchy)
    @GetMapping("/corps")
    public List<DropdownDTO> getCorps(@RequestParam(required = false) Long commandId) {
        // If commandId is null, return empty list or handle as per business logic
        if (commandId == null) {
            return List.of(); // Return empty list when no command selected
        }
        return service.getCorpsByCommandId(commandId);
    }

    // Divisions filtered by corpsId (required for proper hierarchy)
    @GetMapping("/divisions")
    public List<DropdownDTO> getDivisions(@RequestParam(required = false) Long corpsId) {
        if (corpsId == null) {
            return List.of(); // Return empty list when no corps selected
        }
        return service.getDivisionsByCorpsId(corpsId);
    }

    // Brigades filtered by divisionId (required for proper hierarchy)
    @GetMapping("/brigades")
    public List<DropdownDTO> getBrigades(@RequestParam(required = false) Long divisionId) {
        if (divisionId == null) {
            return List.of(); // Return empty list when no division selected
        }
        return service.getBrigadesByDivisionId(divisionId);
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
