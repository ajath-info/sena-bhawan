package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.OfficerListRequestDto;
import com.example.sena_bhawan.dto.OfficerListResponseDto;
import com.example.sena_bhawan.dto.OfficerSummaryResponseDto;
import com.example.sena_bhawan.entity.Officer;
import com.example.sena_bhawan.entity.Personnel;

import java.util.List;

public interface OfficerService {

    List<Officer> getAllOfficers();

    Officer getOfficerById(Integer id);

    long getOfficerCount();

    List<Officer> getOfficersByStatus(String status);

    List<Officer> getOfficersByRank(String rank);

    List<Officer> getOfficersByUnit(String unit);
}
