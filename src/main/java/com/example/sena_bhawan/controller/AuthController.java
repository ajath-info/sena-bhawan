package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@RestController
@Configuration
public class AuthController implements WebMvcConfigurer, HandlerInterceptor {

    private final AdminService adminService;

    public AuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    // --------------------------------------
    // ✅ LoginRequest INNER CLASS
    // --------------------------------------
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // --------------------------------------
    // ✅ LOGIN API
    // --------------------------------------
    @PostMapping(
            value = "/login",
            consumes = "application/json"
    )
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        boolean valid = adminService.authenticate(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        if (!valid) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid credentials"));
        }

        request.getSession(true).setAttribute("user", loginRequest.getUsername());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // --------------------------------------
    // ✅ SESSION CHECK
    // --------------------------------------
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            return ResponseEntity.ok(Map.of("ok", true));
        }
        return ResponseEntity.status(401).build();
    }

    // --------------------------------------
    // ✅ INTERCEPTOR (same file)
    // --------------------------------------
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        // allow login API + login page
        if (uri.endsWith("login.html") || uri.equals("/login")) {
            return true;
        }

        // check session
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            return true;
        }

        // block and redirect
        response.sendRedirect("/login.html");
        return false;
    }

    // --------------------------------------
    // ✅ REGISTER INTERCEPTOR
    // --------------------------------------
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this)
                .addPathPatterns("/**/*.html");
    }
}
