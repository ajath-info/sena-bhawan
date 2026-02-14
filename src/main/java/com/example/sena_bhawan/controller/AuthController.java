package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.LoginRequest;
import com.example.sena_bhawan.dto.LoginResponse;
import com.example.sena_bhawan.entity.UserMaster;
import com.example.sena_bhawan.service.AdminService;
import com.example.sena_bhawan.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

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

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("appointment", user.getAppointment());
        session.setAttribute("token", token);

        return ResponseEntity.ok(
                new LoginResponse(
                        user.getUserId(),
                        user.getUsername(),
                        user.getAppointment(),
                        token
                )
        );
    }
}
