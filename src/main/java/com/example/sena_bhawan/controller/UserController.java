package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.UserCreateRequest;
import com.example.sena_bhawan.dto.SusDropdownDTO;
import com.example.sena_bhawan.entity.RoleMaster;
import com.example.sena_bhawan.entity.UserMaster;
import com.example.sena_bhawan.entity.AppointmentMaster;
import com.example.sena_bhawan.service.RoleService;
import com.example.sena_bhawan.service.UserService;
import com.example.sena_bhawan.service.AppointmentService;
import com.example.sena_bhawan.service.OrbatService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final AppointmentService appointmentService;
    private final OrbatService orbatService;

    public UserController(UserService userService,
                          RoleService roleService,
                          AppointmentService appointmentService,
                          OrbatService orbatService) {
        this.userService = userService;
        this.roleService = roleService;
        this.appointmentService = appointmentService;
        this.orbatService = orbatService;
    }

    // ================= USERS =================

    @PostMapping("/users")
    public UserMaster createUser(@RequestBody UserCreateRequest req) {
        return userService.createUser(req);
    }

    @GetMapping("/users")
    public List<UserMaster> getUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/users/{id}")
    public UserMaster updateUser(@PathVariable Long id,
                                 @RequestBody UserCreateRequest req) {
        return userService.updateUser(id, req);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // ================= ROLES =================

    @GetMapping("/roles")
    public List<RoleMaster> getRoles() {
        return roleService.getAllRoles();
    }

    // ================= APPOINTMENTS =================

    @GetMapping("/appointments")
    public List<AppointmentMaster> getAppointments() {
        return appointmentService.getAllAppointments();
    }

    @PostMapping("/appointments")
    public AppointmentMaster createAppointment(
            @RequestBody AppointmentMaster appointment) {
        return appointmentService.createAppointment(
                appointment.getAppointmentName());
    }


}
