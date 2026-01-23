package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.Role;
import com.example.sena_bhawan.service.RoleCrud;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin("*")
public class RoleController {

    @Autowired
    private RoleCrud service;

    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return service.getRoleById(id);
    }

    @PostMapping
    public Role addRole(@RequestBody Role role) {
        return service.addRole(role);
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Long id, @RequestBody Role role) {
        return service.updateRole(id, role);
    }

    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable Long id) {
        service.deleteRole(id);
        return "Role deleted successfully!";
    }
}
