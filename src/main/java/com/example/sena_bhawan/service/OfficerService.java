package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.Officer;

import java.util.List;

public interface OfficerService {

    List<Officer> getAllOfficers();

    Officer getOfficerById(Integer id);

    long getOfficerCount();

    List<Officer> getOfficersByStatus(String status);

    List<Officer> getOfficersByRank(String rank);

    List<Officer> getOfficersByUnit(String unit);
}
