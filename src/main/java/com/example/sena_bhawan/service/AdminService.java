package com.example.sena_bhawan.service;

import com.example.sena_bhawan.repository.UserMasterRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserMasterRepository repository;

    public AdminService(UserMasterRepository repository) {
        this.repository = repository;
    }

    public boolean authenticate(String username, String password) {
        return repository.findByUsername(username)
                .map(UserMaster -> UserMaster.getPassword().equals(password))
                .orElse(false);
    }
}
