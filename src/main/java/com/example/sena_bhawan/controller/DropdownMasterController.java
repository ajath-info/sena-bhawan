package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.DropdownDTO;
import com.example.sena_bhawan.service.impl.MasterDropdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dropdown")
public class DropdownMasterController {
    @Autowired
    MasterDropdownService masterDropdownService;

    @GetMapping("/{type}")
    public List<DropdownDTO> getByType(@PathVariable String type) {
        return masterDropdownService.getByType(type.toUpperCase());
    }
}
