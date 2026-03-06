package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.EstablishmentRequest;
import com.example.sena_bhawan.dto.EstablishmentResponse;
import com.example.sena_bhawan.entity.FormationEstablishment;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.entity.UnitMaster;
import com.example.sena_bhawan.repository.FormationEstablishmentRepository;
import com.example.sena_bhawan.repository.OrbatRepository;
import com.example.sena_bhawan.repository.OrbatStructureRepository;
import com.example.sena_bhawan.repository.UnitMasterRepository;
import com.example.sena_bhawan.service.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class EstablishmentImpl implements EstablishmentService {

    @Autowired
    private FormationEstablishmentRepository repository;

    @Autowired
    private OrbatRepository orbatRepository;

    @Override
    public EstablishmentResponse getByOrbatAndType(
            Long orbatId,
            FormationEstablishment.EstablishmentType type) {

        Optional<FormationEstablishment> optional =
                repository.findByOrbatIdAndEstablishmentType(orbatId, type);

        FormationEstablishment entity =
                optional.orElse(new FormationEstablishment());

        EstablishmentResponse response = new EstablishmentResponse();
        response.setEstablishmentType(type);
        response.setOrbatId(orbatId);
        response.setFormationType(entity.getFormationType());
        response.setName(entity.getName());

        // ===== Authorized =====
        EstablishmentResponse.Authorized auth =
                new EstablishmentResponse.Authorized();

        auth.setLtCapt(nullSafe(entity.getAuthLtCapt()));
        auth.setMaj(nullSafe(entity.getAuthMaj()));
        auth.setLtCol(nullSafe(entity.getAuthLtCol()));
        auth.setCol(nullSafe(entity.getAuthCol()));
        auth.setBrig(nullSafe(entity.getAuthBrig()));
        auth.setMajGen(nullSafe(entity.getAuthMajGen()));
        auth.setLtGen(nullSafe(entity.getAuthLtGen()));

        auth.setTotalAuthOfficers(
                auth.getLtCapt() +
                        auth.getMaj() +
                        auth.getLtCol() +
                        auth.getCol() +
                        auth.getBrig() +
                        auth.getMajGen() +
                        auth.getLtGen()
        );

        response.setAuthorized(auth);

        // ===== Hard Scale =====
        EstablishmentResponse.HardScale hard =
                new EstablishmentResponse.HardScale();

        hard.setLtCapt(nullSafe(entity.getHardLtCapt()));
        hard.setMaj(nullSafe(entity.getHardMaj()));
        hard.setLtCol(nullSafe(entity.getHardLtCol()));
        hard.setCol(nullSafe(entity.getHardCol()));
        hard.setBrig(nullSafe(entity.getHardBrig()));
        hard.setMajGen(nullSafe(entity.getHardMajGen()));
        hard.setLtGen(nullSafe(entity.getHardLtGen()));

        hard.setTotalHardScale(
                hard.getLtCapt() +
                        hard.getMaj() +
                        hard.getLtCol() +
                        hard.getCol() +
                        hard.getBrig() +
                        hard.getMajGen() +
                        hard.getLtGen()
        );

        response.setHardScale(hard);

        return response;
    }

    @Override
    public String updateEstablishment(
            Long orbatId,
            FormationEstablishment.EstablishmentType type,
            EstablishmentRequest request) {

        FormationEstablishment entity =
                repository.findByOrbatIdAndEstablishmentType(orbatId, type)
                        .orElse(new FormationEstablishment());

        // 🔥 fetch from orbat table
        OrbatStructure orbat =
                orbatRepository.findById(orbatId)
                        .orElseThrow(() ->
                                new RuntimeException("Orbat not found"));

        entity.setOrbatId(orbatId);
        entity.setFormationType(orbat.getFormationType());
        entity.setName(orbat.getName());
        entity.setEstablishmentType(type);

        // Authorized
        entity.setAuthLtCapt(safe(request.getLtCapt()));
        entity.setAuthMaj(safe(request.getMaj()));
        entity.setAuthLtCol(safe(request.getLtCol()));
        entity.setAuthCol(safe(request.getCol()));
        entity.setAuthBrig(safe(request.getBrig()));
        entity.setAuthMajGen(safe(request.getMajGen()));
        entity.setAuthLtGen(safe(request.getLtGen()));

        entity.setTotalAuthOfficers(safe(request.getTotalAuthorizedOfficers()));

        // Hard Scale
        entity.setHardLtCapt(safe(request.getHsLtCapt()));
        entity.setHardMaj(safe(request.getHsMaj()));
        entity.setHardLtCol(safe(request.getHsLtCol()));
        entity.setHardCol(safe(request.getHsCol()));
        entity.setHardBrig(safe(request.getHsBrig()));
        entity.setHardMajGen(safe(request.getHsMajGen()));
        entity.setHardLtGen(safe(request.getHsLtGen()));

        entity.setTotalHardScale(safe(request.getTotalHardScale()));

        repository.save(entity);

        return "Establishment saved successfully";
    }

    private int nullSafe(Integer value) {
        return value == null ? 0 : value;
    }

    private Integer safe(Integer value) {
        return value == null ? 0 : value;
    }
}