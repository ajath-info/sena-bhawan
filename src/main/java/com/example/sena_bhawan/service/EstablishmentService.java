package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.EstablishmentRequest;
import com.example.sena_bhawan.dto.EstablishmentResponse;
import com.example.sena_bhawan.entity.FormationEstablishment;

public interface EstablishmentService {
    EstablishmentResponse getByOrbatAndType(Long orbatId, FormationEstablishment.EstablishmentType type);

    String updateEstablishment(Long orbatId, FormationEstablishment.EstablishmentType type, EstablishmentRequest request);
}
