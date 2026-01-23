package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.DecorationMaster;
import com.example.sena_bhawan.service.DecorationMasterService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/decoration-master")
@CrossOrigin
public class DecorationMasterController {

    private final DecorationMasterService service;

    public DecorationMasterController(DecorationMasterService service) {
        this.service = service;
    }

    // ADD
    @PostMapping
    public DecorationMaster add(@RequestBody DecorationMaster decoration) {
        return service.addDecoration(decoration);
    }

    // UPDATE
    @PutMapping("/{id}")
    public DecorationMaster update(@PathVariable Long id, @RequestBody DecorationMaster decoration) {
        return service.updateDecoration(id, decoration);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteDecoration(id);
        return "Deleted Successfully";
    }

    // GET BY ID
    @GetMapping("/{id}")
    public DecorationMaster getById(@PathVariable Long id) {
        return service.getDecorationById(id);
    }

    // GET ALL
    @GetMapping
    public List<DecorationMaster> getAll() {
        return service.getAllDecorations();
    }

    // DROPDOWN API
    @GetMapping("/dropdown")
    public List<Object> dropdown() {
        return service.getDecorationDropdown();
    }

    // DROPDOWN — All Categories
    @GetMapping("/dropdown/categories")
    public List<String> getCategories() {
        return service.getAllCategories();
    }

    // DROPDOWN — Awards by Category
    @GetMapping("/dropdown/awards/{category}")
    public List<Object> getAwardsByCategory(@PathVariable String category) {
        return service.getAwardsByCategory(category);
    }

}
