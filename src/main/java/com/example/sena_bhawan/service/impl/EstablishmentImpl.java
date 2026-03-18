package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.EstablishmentRequest;
import com.example.sena_bhawan.dto.EstablishmentResponse;
import com.example.sena_bhawan.entity.FormationEstablishment;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.repository.FormationEstablishmentRepository;
import com.example.sena_bhawan.repository.OrbatRepository;
import com.example.sena_bhawan.service.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EstablishmentImpl implements EstablishmentService {

    @Autowired
    private FormationEstablishmentRepository repository;

    @Autowired
    private OrbatRepository orbatRepository;

    @Override
    public List<String> getEstablishmentName() {
        try {
            return repository.listOfEstablishmentType();
        } catch (Exception e) {
            System.err.println("Error fetching establishment types: " + e.getMessage());
            return new ArrayList<>(); // Return empty list on error
        }
    }

    @Override
    public EstablishmentResponse getByOrbatId(
            Long orbatId,
            FormationEstablishment.EstablishmentType type) {

        // Validate input
        if (orbatId == null) {
            throw new IllegalArgumentException("ORBAT ID cannot be null");
        }

        try {
            Optional<FormationEstablishment> optional = repository.findByOrbatId(orbatId);
            FormationEstablishment entity = optional.orElse(new FormationEstablishment());

            EstablishmentResponse response = new EstablishmentResponse();

            // Set establishment type
            if (type != null) {
                response.setEstablishmentType(type);
            } else if (entity.getEstablishmentType() != null) {
                response.setEstablishmentType(entity.getEstablishmentType());
            }

            response.setOrbatId(orbatId);
            response.setFormationType(entity.getFormationType());
            response.setName(entity.getName());

            // ===== Authorized =====
            EstablishmentResponse.Authorized auth = new EstablishmentResponse.Authorized();

            try {
                auth.setLtCapt(nullSafe(entity.getAuthLtCapt()));
                auth.setMaj(nullSafe(entity.getAuthMaj()));
                auth.setLtCol(nullSafe(entity.getAuthLtCol()));
                auth.setCol(nullSafe(entity.getAuthCol()));
                auth.setBrig(nullSafe(entity.getAuthBrig()));
                auth.setMajGen(nullSafe(entity.getAuthMajGen()));
                auth.setLtGen(nullSafe(entity.getAuthLtGen()));

                auth.setTotalAuthOfficers(
                        auth.getLtCapt() + auth.getMaj() + auth.getLtCol() +
                                auth.getCol() + auth.getBrig() + auth.getMajGen() +
                                auth.getLtGen()
                );
            } catch (Exception e) {
                System.err.println("Error processing authorized data: " + e.getMessage());
                // Set default values on error
                auth.setLtCapt(0);
                auth.setMaj(0);
                auth.setLtCol(0);
                auth.setCol(0);
                auth.setBrig(0);
                auth.setMajGen(0);
                auth.setLtGen(0);
                auth.setTotalAuthOfficers(0);
            }

            response.setAuthorized(auth);

            // ===== Hard Scale =====
            EstablishmentResponse.HardScale hard = new EstablishmentResponse.HardScale();

            try {
                hard.setLtCapt(nullSafe(entity.getHardLtCapt()));
                hard.setMaj(nullSafe(entity.getHardMaj()));
                hard.setLtCol(nullSafe(entity.getHardLtCol()));
                hard.setCol(nullSafe(entity.getHardCol()));
                hard.setBrig(nullSafe(entity.getHardBrig()));
                hard.setMajGen(nullSafe(entity.getHardMajGen()));
                hard.setLtGen(nullSafe(entity.getHardLtGen()));

                hard.setTotalHardScale(
                        hard.getLtCapt() + hard.getMaj() + hard.getLtCol() +
                                hard.getCol() + hard.getBrig() + hard.getMajGen() +
                                hard.getLtGen()
                );
            } catch (Exception e) {
                System.err.println("Error processing hard scale data: " + e.getMessage());
                // Set default values on error
                hard.setLtCapt(0);
                hard.setMaj(0);
                hard.setLtCol(0);
                hard.setCol(0);
                hard.setBrig(0);
                hard.setMajGen(0);
                hard.setLtGen(0);
                hard.setTotalHardScale(0);
            }

            response.setHardScale(hard);

            return response;

        } catch (DataAccessException e) {
            System.err.println("Database error while fetching establishment data for ORBAT ID " + orbatId + ": " + e.getMessage());
            throw new RuntimeException("Unable to fetch establishment data due to database error", e);
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching establishment data for ORBAT ID " + orbatId + ": " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching establishment data", e);
        }
    }

    @Override
    @Transactional
    public String updateEstablishment(Long orbatId, EstablishmentRequest request) {

        // Validate inputs
        if (orbatId == null) {
            throw new IllegalArgumentException("ORBAT ID cannot be null");
        }

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        // Validate numeric fields
        try {
            validateNumericFields(request);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Validation failed: " + e.getMessage());
        }

        try {
            // Find by ORBAT ID only
            FormationEstablishment entity;
            try {
                entity = repository.findByOrbatId(orbatId)
                        .orElse(new FormationEstablishment());
            } catch (DataAccessException e) {
                System.err.println("Database error while checking existing establishment: " + e.getMessage());
                throw new RuntimeException("Unable to check existing establishment data", e);
            }

            // Fetch from orbat table
            OrbatStructure orbat;
            try {
                orbat = orbatRepository.findById(orbatId)
                        .orElseThrow(() -> new RuntimeException("Orbat not found with ID: " + orbatId));
            } catch (DataAccessException e) {
                System.err.println("Database error while fetching ORBAT data: " + e.getMessage());
                throw new RuntimeException("Unable to fetch ORBAT details", e);
            }

            // Set basic info
            try {
                entity.setOrbatId(orbatId);
                entity.setFormationType(orbat.getFormationType());
                entity.setName(orbat.getName());

                // Set establishment type
                if (request.getEstablishmentType() != null) {
                    entity.setEstablishmentType(request.getEstablishmentType());
                }
            } catch (Exception e) {
                System.err.println("Error setting basic entity data: " + e.getMessage());
                throw new RuntimeException("Failed to set basic establishment data", e);
            }

            // Set authorized fields
            try {
                entity.setAuthLtCapt(safe(request.getLtCapt()));
                entity.setAuthMaj(safe(request.getMaj()));
                entity.setAuthLtCol(safe(request.getLtCol()));
                entity.setAuthCol(safe(request.getCol()));
                entity.setAuthBrig(safe(request.getBrig()));
                entity.setAuthMajGen(safe(request.getMajGen()));
                entity.setAuthLtGen(safe(request.getLtGen()));

                // Calculate total if not provided or validate
                if (request.getTotalAuthorizedOfficers() != null && request.getTotalAuthorizedOfficers() > 0) {
                    entity.setTotalAuthOfficers(safe(request.getTotalAuthorizedOfficers()));
                } else {
                    int calculatedTotal = safe(request.getLtCapt()) + safe(request.getMaj()) +
                            safe(request.getLtCol()) + safe(request.getCol()) +
                            safe(request.getBrig()) + safe(request.getMajGen()) +
                            safe(request.getLtGen());
                    entity.setTotalAuthOfficers(calculatedTotal);
                }
            } catch (Exception e) {
                System.err.println("Error setting authorized fields: " + e.getMessage());
                throw new RuntimeException("Failed to set authorized strength data", e);
            }

            // Set hard scale fields
            try {
                entity.setHardLtCapt(safe(request.getHsLtCapt()));
                entity.setHardMaj(safe(request.getHsMaj()));
                entity.setHardLtCol(safe(request.getHsLtCol()));
                entity.setHardCol(safe(request.getHsCol()));
                entity.setHardBrig(safe(request.getHsBrig()));
                entity.setHardMajGen(safe(request.getHsMajGen()));
                entity.setHardLtGen(safe(request.getHsLtGen()));

                // Calculate total if not provided or validate
                if (request.getTotalHardScale() != null && request.getTotalHardScale() > 0) {
                    entity.setTotalHardScale(safe(request.getTotalHardScale()));
                } else {
                    int calculatedTotal = safe(request.getHsLtCapt()) + safe(request.getHsMaj()) +
                            safe(request.getHsLtCol()) + safe(request.getHsCol()) +
                            safe(request.getHsBrig()) + safe(request.getHsMajGen()) +
                            safe(request.getHsLtGen());
                    entity.setTotalHardScale(calculatedTotal);
                }
            } catch (Exception e) {
                System.err.println("Error setting hard scale fields: " + e.getMessage());
                throw new RuntimeException("Failed to set hard scale data", e);
            }

            // Save to database
            try {
                repository.save(entity);
            } catch (DataIntegrityViolationException e) {
                System.err.println("Data integrity violation while saving: " + e.getMessage());
                if (e.getMessage().contains("unique constraint") || e.getMessage().contains("duplicate")) {
                    throw new RuntimeException("A record already exists for this unit with different parameters", e);
                }
                throw new RuntimeException("Data integrity error while saving establishment", e);
            } catch (DataAccessException e) {
                System.err.println("Database error while saving: " + e.getMessage());
                throw new RuntimeException("Unable to save establishment data due to database error", e);
            }

            return "Establishment saved successfully for unit: " + orbat.getName();

        } catch (IllegalArgumentException e) {
            // Re-throw validation errors
            throw e;
        } catch (RuntimeException e) {
            // Re-throw runtime exceptions with their messages
            System.err.println("Runtime error in updateEstablishment: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.err.println("Unexpected error in updateEstablishment: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while updating establishment", e);
        }
    }


    private void validateNumericFields(EstablishmentRequest request) {
        StringBuilder errors = new StringBuilder();

        // Check authorized fields
        checkNegative(request.getLtCapt(), "Lt/Capt", errors);
        checkNegative(request.getMaj(), "Maj", errors);
        checkNegative(request.getLtCol(), "Lt Col", errors);
        checkNegative(request.getCol(), "Col", errors);
        checkNegative(request.getBrig(), "Brig", errors);
        checkNegative(request.getMajGen(), "Maj Gen", errors);
        checkNegative(request.getLtGen(), "Lt Gen", errors);
        checkNegative(request.getTotalAuthorizedOfficers(), "Total Authorized Officers", errors);

        // Check hard scale fields
        checkNegative(request.getHsLtCapt(), "Hard Scale Lt/Capt", errors);
        checkNegative(request.getHsMaj(), "Hard Scale Maj", errors);
        checkNegative(request.getHsLtCol(), "Hard Scale Lt Col", errors);
        checkNegative(request.getHsCol(), "Hard Scale Col", errors);
        checkNegative(request.getHsBrig(), "Hard Scale Brig", errors);
        checkNegative(request.getHsMajGen(), "Hard Scale Maj Gen", errors);
        checkNegative(request.getHsLtGen(), "Hard Scale Lt Gen", errors);
        checkNegative(request.getTotalHardScale(), "Total Hard Scale", errors);

        if (errors.length() > 0) {
            throw new IllegalArgumentException(errors.toString());
        }
    }


    private void checkNegative(Integer value, String fieldName, StringBuilder errors) {
        if (value != null && value < 0) {
            if (errors.length() > 0) errors.append("; ");
            errors.append(fieldName).append(" cannot be negative");
        }
    }


    private int nullSafe(Integer value) {
        try {
            return value == null ? 0 : value;
        } catch (Exception e) {
            System.err.println("Error in nullSafe conversion: " + e.getMessage());
            return 0;
        }
    }


    private Integer safe(Integer value) {
        try {
            return value == null ? 0 : value;
        } catch (Exception e) {
            System.err.println("Error in safe conversion: " + e.getMessage());
            return 0;
        }
    }
}