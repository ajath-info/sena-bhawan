package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.UserCreateRequest;
import com.example.sena_bhawan.entity.UserMaster;
import com.example.sena_bhawan.entity.UserRoleInfo;
import com.example.sena_bhawan.repository.UserMasterRepository;
import com.example.sena_bhawan.repository.UserRoleInfoRepository;
import com.example.sena_bhawan.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMasterRepository userRepo;
    private final UserRoleInfoRepository userRoleRepo;

    public UserServiceImpl(UserMasterRepository userRepo,
                           UserRoleInfoRepository userRoleRepo) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
    }

    @Override
    public UserMaster createUser(UserCreateRequest req) {

        UserMaster user = new UserMaster();
        user.setUsername(req.getUsername());
        user.setAppointment(req.getAppointment());
        user.setPassword(req.getPassword());
        user.setSosNo(req.getSosNo());
//        user.setUnitName(req.getUnitName());

        UserMaster savedUser = userRepo.save(user);

        for (Long roleId : req.getRoleIds()) {
            UserRoleInfo map = new UserRoleInfo();
            map.setUserId(savedUser.getUserId());
            map.setRoleId(roleId);
            userRoleRepo.save(map);
        }

        return savedUser;
    }

    @Override
    public List<UserMaster> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserMaster updateUser(Long userId, UserCreateRequest req) {

        UserMaster user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(req.getUsername());
        user.setAppointment(req.getAppointment());
        user.setPassword(req.getPassword());
        user.setSosNo(req.getSosNo());
//        user.setUnitName(req.getUnitName());

        userRoleRepo.findByUserId(userId)
                .forEach(userRoleRepo::delete);

        for (Long roleId : req.getRoleIds()) {
            userRoleRepo.save(new UserRoleInfo(null, userId, roleId));
        }

        return userRepo.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepo.deleteById(userId);
    }
}

