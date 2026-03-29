package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.UserRoleInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleInfoRepository
        extends JpaRepository<UserRoleInfo, Long> {

    UserRoleInfo findByUserId(Long userId);
}

