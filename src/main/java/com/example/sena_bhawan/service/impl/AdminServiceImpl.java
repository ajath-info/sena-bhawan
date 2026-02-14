package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.UserMaster;
import com.example.sena_bhawan.repository.UserMasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMasterRepository userRepo;

    @Override
    public UserMaster getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    @Override
    public boolean authenticate(String username, String password) {
        Optional<UserMaster> userOpt = userRepo.findByUsername(username);

        return userOpt.isPresent()
                && userOpt.get().getPassword().equals(password);
    }
}
