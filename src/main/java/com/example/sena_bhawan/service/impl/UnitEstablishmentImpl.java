package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.UnitEstablishmentRequest;
import com.example.sena_bhawan.dto.UnitEstablishmentResponse;
import com.example.sena_bhawan.entity.UnitEstablishment;
import com.example.sena_bhawan.entity.UnitMaster;
import com.example.sena_bhawan.repository.UnitEstablishmentRepository;
import com.example.sena_bhawan.repository.UnitMasterRepository;
import com.example.sena_bhawan.service.UnitEstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UnitEstablishmentImpl implements UnitEstablishmentService {
    @Autowired
    UnitEstablishmentRepository unitEstablishmentRepository;

    @Autowired
    UnitMasterRepository unitMasterRepository;

    public UnitEstablishmentResponse getByUnitAndType(Long unitId, UnitEstablishment.EstablishmentType type) {

        Optional<UnitEstablishment> optional =
                unitEstablishmentRepository
                        .findByUnitId_IdAndEstablishmentType(unitId, type);

        UnitEstablishment entity = optional.orElse(new UnitEstablishment());

        UnitEstablishmentResponse response =
                new UnitEstablishmentResponse();

        response.setUnitId(unitId);
        response.setEstablishmentType(type);

        // -------- Authorized --------
        UnitEstablishmentResponse.Authorized auth =
                new UnitEstablishmentResponse.Authorized();

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

        // -------- Hard Scale --------
        UnitEstablishmentResponse.HardScale hard =
                new UnitEstablishmentResponse.HardScale();

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

    private int nullSafe(Integer value) {
        return value == null ? 0 : value;
    }


    public String updateUnitEstablishment(
            Long unitId,
            UnitEstablishment.EstablishmentType type,
            UnitEstablishmentRequest request) {

        try {
            // Try to find existing record by unitId only (not by type)
            UnitEstablishment entity = unitEstablishmentRepository.findByUnitId(unitId);

            if (entity != null) {
                // UPDATE existing record
                entity.setEstablishmentType(type); // Update the type if needed

                entity.setTotalAuthOfficers(safeValue(request.getTotalAuthorizedOfficers()));
                entity.setTotalHardScale(safeValue(request.getTotalHardScale()));

                // Authorized
                entity.setAuthLtCapt(safeValue(request.getLtCapt()));
                entity.setAuthMaj(safeValue(request.getMaj()));
                entity.setAuthLtCol(safeValue(request.getLtCol()));
                entity.setAuthCol(safeValue(request.getCol()));
                entity.setAuthBrig(safeValue(request.getBrig()));
                entity.setAuthMajGen(safeValue(request.getMajGen()));
                entity.setAuthLtGen(safeValue(request.getLtGen()));

                // Hard Scale
                entity.setHardLtCapt(safeValue(request.getHsLtCapt()));
                entity.setHardMaj(safeValue(request.getHsMaj()));
                entity.setHardLtCol(safeValue(request.getHsLtCol()));
                entity.setHardCol(safeValue(request.getHsCol()));
                entity.setHardBrig(safeValue(request.getHsBrig()));
                entity.setHardMajGen(safeValue(request.getHsMajGen()));
                entity.setHardLtGen(safeValue(request.getHsLtGen()));

                unitEstablishmentRepository.save(entity);

                return "Unit establishment updated successfully";

            } else {
                // CREATE new record
                UnitEstablishment newEntity = new UnitEstablishment();

                // You need to fetch the UnitMaster entity
                UnitMaster unitMaster = unitMasterRepository.findById(unitId)
                        .orElseThrow(() -> new RuntimeException("Unit not found with id: " + unitId));

                newEntity.setUnitId(unitMaster);
                newEntity.setEstablishmentType(type);

                newEntity.setTotalAuthOfficers(safeValue(request.getTotalAuthorizedOfficers()));
                newEntity.setTotalHardScale(safeValue(request.getTotalHardScale()));

                // Authorized
                newEntity.setAuthLtCapt(safeValue(request.getLtCapt()));
                newEntity.setAuthMaj(safeValue(request.getMaj()));
                newEntity.setAuthLtCol(safeValue(request.getLtCol()));
                newEntity.setAuthCol(safeValue(request.getCol()));
                newEntity.setAuthBrig(safeValue(request.getBrig()));
                newEntity.setAuthMajGen(safeValue(request.getMajGen()));
                newEntity.setAuthLtGen(safeValue(request.getLtGen()));

                // Hard Scale
                newEntity.setHardLtCapt(safeValue(request.getHsLtCapt()));
                newEntity.setHardMaj(safeValue(request.getHsMaj()));
                newEntity.setHardLtCol(safeValue(request.getHsLtCol()));
                newEntity.setHardCol(safeValue(request.getHsCol()));
                newEntity.setHardBrig(safeValue(request.getHsBrig()));
                newEntity.setHardMajGen(safeValue(request.getHsMajGen()));
                newEntity.setHardLtGen(safeValue(request.getHsLtGen()));

                unitEstablishmentRepository.save(newEntity);

                return "Unit establishment created successfully";
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing unit establishment: " + e.getMessage(), e);
        }
    }

    private Integer safeValue(Integer value) {
        return value == null ? 0 : value;
    }

}
