package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.EstablishmentRequest;
import com.example.sena_bhawan.dto.EstablishmentResponse;
import com.example.sena_bhawan.entity.FormationEstablishment;

import java.util.List;

public interface EstablishmentService {
    List<String> getEstablishmentName();

    EstablishmentResponse getByOrbatId(Long orbatId, FormationEstablishment.EstablishmentType type);

    String updateEstablishment(Long orbatId, EstablishmentRequest request);
}
