package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.LoginRequest;
import com.example.sena_bhawan.dto.LoginResponse;
import com.example.sena_bhawan.entity.Role;
import com.example.sena_bhawan.entity.UserMaster;
import com.example.sena_bhawan.entity.UserRoleInfo;
import com.example.sena_bhawan.repository.RoleRepository;
import com.example.sena_bhawan.repository.UserRoleInfoRepository;
import com.example.sena_bhawan.service.AdminService;
import com.example.sena_bhawan.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRoleInfoRepository userRoleInfoRepository;
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        UserMaster user = adminService.getUserByUsername(loginRequest.getUsername());

        if (user == null || !adminService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
            return ResponseEntity.status(401)
                    .body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId());

        UserRoleInfo roleIds = userRoleInfoRepository.findByUserId(user.getUserId());
        Role role = roleRepository.findById(roleIds.getRoleId()).orElseThrow();

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("appointment", user.getAppointment());
        session.setAttribute("token", token);
        session.setAttribute("roleId", role.getHierarchyOrder());

        return ResponseEntity.ok(
                new LoginResponse(
                        user.getUserId(),
                        user.getUsername(),
                        user.getAppointment(),
                        token,
                        role.getHierarchyOrder()
                )
        );
    }
}
