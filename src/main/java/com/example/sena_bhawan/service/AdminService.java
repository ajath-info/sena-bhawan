package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.UserMaster;

public interface AdminService {
    UserMaster getUserByUsername(String username);
    boolean authenticate(String username, String password);
}
