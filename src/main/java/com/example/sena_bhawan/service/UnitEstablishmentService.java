package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.UnitEstablishmentRequest;
import com.example.sena_bhawan.dto.UnitEstablishmentResponse;
import com.example.sena_bhawan.entity.UnitEstablishment;
import org.springframework.http.ResponseEntity;

public interface UnitEstablishmentService {
    UnitEstablishmentResponse getByUnitAndType( Long unitId, UnitEstablishment.EstablishmentType type);
    String updateUnitEstablishment(Long unitId, UnitEstablishment.EstablishmentType type, UnitEstablishmentRequest request);
}
