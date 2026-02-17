package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.UnitEstablishmentRequest;
import com.example.sena_bhawan.dto.UnitEstablishmentResponse;
import com.example.sena_bhawan.entity.UnitEstablishment;
import com.example.sena_bhawan.service.UnitEstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unit-establishment")
public class UnitEstablishmentController {

    @Autowired
    UnitEstablishmentService unitEstablishmentService;

    @GetMapping("/{unitId}/{type}")
    public ResponseEntity<?> getByUnitAndType(@PathVariable Long unitId, @PathVariable UnitEstablishment.EstablishmentType type) {

        UnitEstablishmentResponse response =
                unitEstablishmentService.getByUnitAndType(unitId, type);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{unitId}/{type}")
    public ResponseEntity<String> updateUnitEstablishment(
            @PathVariable Long unitId,
            @PathVariable UnitEstablishment.EstablishmentType type,
            @RequestBody UnitEstablishmentRequest request) {

        return ResponseEntity.ok(
                unitEstablishmentService.updateUnitEstablishment(unitId, type, request)
        );
    }



}
