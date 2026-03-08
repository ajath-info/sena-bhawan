package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.PostingRequestDTO;
import com.example.sena_bhawan.dto.PostingResponseDTO;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.entity.PostingDetails;
import com.example.sena_bhawan.repository.OrbatStructureRepository;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.repository.PostingDetailsRepository;
import com.example.sena_bhawan.service.PostingDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostingDetailsServiceImpl implements PostingDetailsService {

    private final PostingDetailsRepository postingRepository;
    private final PersonnelRepository personnelRepository;
    private final OrbatStructureRepository orbatRepository;

    @Override
    public List<PostingDetails> getByPersonnel(Long personnelId) {
        return postingRepository.findByPersonnelId(personnelId);
    }

    @Override
    @Transactional
    public PostingDetails upsertPosting(PostingRequestDTO dto) {

        // CHANGE: Find by armyNo instead of ID
        Personnel personnel = personnelRepository.findByArmyNo(dto.getArmyNo())
                .orElseThrow(() -> new RuntimeException(
                        "Personnel not found with Army No: " + dto.getArmyNo()));
        // Rest of your code remains EXACTLY same
        log.info("Processing posting for Army No: {}, Name: {}",
                personnel.getArmyNo(), personnel.getFullName());

        // Step 2: Validate and get ORBAT ID for postedTo formation
        if (dto.getPostedTo() == null || dto.getPostedTo().trim().isEmpty()) {
            throw new RuntimeException("Posted To field cannot be empty");
        }

        // Get ORBAT ID from validation
        Long validatedOrbatId = validateAndGetOrbatId(dto.getPostedTo());
        dto.setOrbatId(validatedOrbatId);

        // Step 3: Check if TOS date is provided (new posting or update?)
        if (dto.getTosUpdatedDate() != null) {
            // TOS date provided - this is a new posting
            return processNewPosting(dto, personnel);
        } else {
            // No TOS date - update existing current posting
            return processUpdatePosting(dto, personnel);
        }
    }

    /**
     * Process a new posting (when TOS date is provided)
     */
    private PostingDetails processNewPosting(PostingRequestDTO dto, Personnel personnel) {
        log.info("Processing NEW posting for personnel ID: {}", personnel.getId());

        // Check if personnel already has a current posting
        postingRepository.findCurrentPosting(personnel.getId()).ifPresent(currentPosting -> {
            log.info("Completing current posting ID: {}", currentPosting.getPostingId());

            // Complete the current posting
            currentPosting.setStatus("POSTED");
            currentPosting.setToDate(dto.getTosUpdatedDate());
            currentPosting.setSosDate(dto.getTosUpdatedDate());

            // Calculate duration
            if (currentPosting.getFromDate() != null) {
                Period period = Period.between(currentPosting.getFromDate(), dto.getTosUpdatedDate());
                String duration = String.format("%d years %d months", period.getYears(), period.getMonths());
                currentPosting.setDuration(duration);
            }

            postingRepository.save(currentPosting);
        });

        // Create new posting record
        PostingDetails newPosting = new PostingDetails();
        newPosting.setPersonnelId(personnel.getId());

        // UNDER POSTING fields
        newPosting.setMovementDate(dto.getMovementDate());
        newPosting.setPostedTo(dto.getPostedTo());
        newPosting.setOrbatId(dto.getOrbatId());  // Use dto.getOrbatId()
        newPosting.setAppointment(dto.getAppointment());
        newPosting.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());

        // POSTING IN fields
        newPosting.setTosUpdatedDate(dto.getTosUpdatedDate());
        newPosting.setRank(dto.getRank());

        // Set from_date = TOS date
        newPosting.setFromDate(dto.getTosUpdatedDate());

        // New posting is current
        newPosting.setStatus("UNDER_POSTING");
        newPosting.setDuration("Present");
        newPosting.setToDate(null);
        newPosting.setSosDate(null);

        // Get formation details from ORBAT using dto.getOrbatId()
        orbatRepository.findById(dto.getOrbatId()).ifPresent(orbat -> {
            newPosting.setFormationType(orbat.getFormationType());
            newPosting.setLocation(orbat.getLocation());
            newPosting.setCommand(orbat.getCommandName());
        });

        PostingDetails saved = postingRepository.save(newPosting);
        log.info("New posting created with ID: {}, Status: UNDER_POSTING", saved.getPostingId());

        return saved;
    }

    /**
     * Process update to existing posting (when no TOS date)
     */
    private PostingDetails processUpdatePosting(PostingRequestDTO dto, Personnel personnel) {
        log.info("Processing UPDATE to current posting for personnel ID: {}", personnel.getId());

        // Find current posting
        PostingDetails currentPosting = postingRepository.findCurrentPosting(personnel.getId())
                .orElseThrow(() -> new RuntimeException(
                        "No current posting found for personnel ID: " + personnel.getId() +
                                ". Please create a new posting first."));

        log.info("Updating current posting ID: {}", currentPosting.getPostingId());

        // Check if unit is changing
        boolean unitChanged = !currentPosting.getPostedTo().equalsIgnoreCase(dto.getPostedTo());
        if (unitChanged) {
            log.info("Unit changing from {} to {}", currentPosting.getPostedTo(), dto.getPostedTo());
            currentPosting.setPostedTo(dto.getPostedTo());
            currentPosting.setOrbatId(dto.getOrbatId());  // Use dto.getOrbatId()

            // Update formation details using dto.getOrbatId()
            orbatRepository.findById(dto.getOrbatId()).ifPresent(orbat -> {
                currentPosting.setFormationType(orbat.getFormationType());
                currentPosting.setLocation(orbat.getLocation());
                currentPosting.setCommand(orbat.getCommandName());
            });
        }

        // Update other fields if provided
        if (dto.getMovementDate() != null) {
            currentPosting.setMovementDate(dto.getMovementDate());
            if (currentPosting.getFromDate() == null) {
                currentPosting.setFromDate(dto.getMovementDate());
            }
        }

        if (dto.getAppointment() != null) {
            currentPosting.setAppointment(dto.getAppointment());
        }

        if (dto.getPostingOrderIssueDate() != null) {
            currentPosting.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());
        }

        if (dto.getRank() != null) {
            currentPosting.setRank(dto.getRank());
        }

        PostingDetails saved = postingRepository.save(currentPosting);
        log.info("Current posting updated successfully");

        return saved;
    }

    @Override
    public Long validateAndGetOrbatId(String formationName) {
        //Case-insensitive search in ORBAT table
        OrbatStructure orbat = orbatRepository.findByFormationNameCaseInsensitive(formationName.trim())
                .orElseThrow(() -> new RuntimeException(
                        "Formation '" + formationName + "' not found in ORBAT master. " +
                                "Please check the formation name or add it to ORBAT first."));

        log.info("Formation validated: {} (ID: {}, Type: {})",
                orbat.getName(), orbat.getId(), orbat.getFormationType());

        return orbat.getId();
    }

    @Override
    public PostingDetails getCurrentPosting(Long personnelId) {
        return postingRepository.findCurrentPosting(personnelId)
                .orElseThrow(() -> new RuntimeException("No current posting found for personnel ID: " + personnelId));
    }

    @Override
    public List<PostingDetails> getPersonnelPostings(Long personnelId) {
        return postingRepository.findByPersonnelIdOrderByFromDateDesc(personnelId);
    }

    @Override
    public PostingResponseDTO getPersonnelPostingSummary(Long personnelId) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        PostingResponseDTO summary = new PostingResponseDTO();
        summary.setPersonnelId(personnelId);
        summary.setArmyNo(personnel.getArmyNo());
        summary.setPersonnelName(personnel.getFullName());
        summary.setRank(personnel.getRank());

        // Get current posting
        postingRepository.findCurrentPosting(personnelId).ifPresent(current -> {
            summary.setPostingId(current.getPostingId());
            summary.setMovementDate(current.getMovementDate());
            summary.setPostedTo(current.getPostedTo());
            summary.setAppointment(current.getAppointment());
            summary.setPostingOrderIssueDate(current.getPostingOrderIssueDate());
            summary.setFromDate(current.getFromDate());
            summary.setStatus(current.getStatus());
            summary.setOrbatId(current.getOrbatId());
        });

        return summary;
    }

    @Override
    @Transactional
    public void deletePosting(Long postingId) {
        PostingDetails posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new RuntimeException("Posting not found with ID: " + postingId));

        if ("UNDER_POSTING".equals(posting.getStatus())) {
            throw new RuntimeException("Cannot delete current posting. Please complete it first by providing TOS date.");
        }

        postingRepository.delete(posting);
        log.info("Deleted posting ID: {}", postingId);
    }

    // Additional method to get by army number
    public List<PostingDetails> getPersonnelPostingsByArmyNo(String armyNo) {
        Personnel personnel = personnelRepository.findByArmyNo(armyNo)
                .orElseThrow(() -> new RuntimeException("Personnel not found with Army No: " + armyNo));
        return getPersonnelPostings(personnel.getId());
    }

    public PostingDetails getCurrentPostingByArmyNo(String armyNo) {
        Personnel personnel = personnelRepository.findByArmyNo(armyNo)
                .orElseThrow(() -> new RuntimeException("Personnel not found with Army No: " + armyNo));
        return getCurrentPosting(personnel.getId());
    }
}