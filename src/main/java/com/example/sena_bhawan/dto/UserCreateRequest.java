package com.example.sena_bhawan.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserCreateRequest {

    private String username;
    private String appointment;
    private String password;

    private String sosNo;
    private String unitName;
    private String name;

    private List<Long> roleIds = new ArrayList<>();
}

