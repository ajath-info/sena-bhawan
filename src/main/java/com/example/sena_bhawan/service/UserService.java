package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.UserCreateRequest;
import com.example.sena_bhawan.entity.UserMaster;

import java.util.List;

public interface UserService {

    UserMaster createUser(UserCreateRequest request);
    List<UserMaster> getAllUsers();
    UserMaster updateUser(Long userId, UserCreateRequest request);
    void deleteUser(Long userId);
}

