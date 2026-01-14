package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.LocationMaster;
import com.example.sena_bhawan.service.LocationMasterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationMasterController {

    private final LocationMasterService service;

    public LocationMasterController(LocationMasterService service) {
        this.service = service;
    }

    // 🔹 GET all locations (already working)
    @GetMapping
    public List<LocationMaster> getAll() {
        return service.getAllLocations();
    }

    @GetMapping("/count")
    public long getLocationCount() {
        return service.getLocationCount();
    }

    // 🔹 ADD new location
    @PostMapping
    public LocationMaster add(@RequestBody LocationMaster location) {
        return service.addLocation(location);
    }

    // 🔹 UPDATE location
    @PutMapping("/{srno}")
    public LocationMaster update(
            @PathVariable Integer srno,
            @RequestBody LocationMaster location
    ) {
        return service.updateLocation(srno, location);
    }
}
