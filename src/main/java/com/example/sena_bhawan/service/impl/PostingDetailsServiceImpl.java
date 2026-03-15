package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.PostingHistoryDTO;
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

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostingDetailsServiceImpl implements PostingDetailsService {

    private final PostingDetailsRepository postingRepository;
    private final PersonnelRepository personnelRepository;
    private final OrbatStructureRepository orbatRepository;

    // ==================== STATUS CONSTANTS ====================
    public static final String UNDER_POSTING = "UNDER_POSTING";
    public static final String POSTED = "POSTED";
    public static final String PREVIOUS_POSTING = "PREVIOUS_POSTING";

    @Override
    public List<PostingDetails> getByPersonnel(Long personnelId) {
        return postingRepository.findByPersonnelId(personnelId);
    }

    // ==================== MAIN UPSERT METHOD ====================
    @Override
    @Transactional
    public PostingDetails upsertPosting(PostingRequestDTO dto) {

        // Step 1: Find Personnel by Army Number
        Personnel personnel = personnelRepository.findByArmyNo(dto.getArmyNo())
                .orElseThrow(() -> new RuntimeException(
                        "Personnel not found with Army No: " + dto.getArmyNo()));

        log.info("Processing posting for Army No: {}, Personnel ID: {}",
                personnel.getArmyNo(), personnel.getId());

        dto.setPersonnelId(personnel.getId());

        // Update rank in personnel table if changed
        if (dto.getRank() != null && !dto.getRank().equals(personnel.getRank())) {
            personnel.setRank(dto.getRank());
            personnelRepository.save(personnel);
            log.info("Updated rank in personnel table: {} → {}",
                    personnel.getArmyNo(), dto.getRank());
        }

        // Step 2: Validate ORBAT
        if (dto.getPostedTo() == null || dto.getPostedTo().trim().isEmpty()) {
            throw new RuntimeException("Posted To field cannot be empty");
        }

        if (dto.getOrbatId() == null) {
            throw new RuntimeException("Invalid formation selection. Please select from recommendations.");
        }

        OrbatStructure orbat = orbatRepository.findById(dto.getOrbatId())
                .orElseThrow(() -> new RuntimeException("Invalid ORBAT ID"));

        // Step 3: Get current active posting
        Optional<PostingDetails> currentActiveOpt = getCurrentActivePosting(personnel.getId());

        // Step 4: Decision Logic based on TOS date
        if (dto.getTosUpdatedDate() != null) {
            return processJoiningPosting(dto, personnel, orbat, currentActiveOpt);
        } else {
            return processUnderPosting(dto, personnel, orbat, currentActiveOpt);
        }
    }

    // ==================== PROCESS JOINING (WITH TOS) ====================
    private PostingDetails processJoiningPosting(PostingRequestDTO dto, Personnel personnel,
                                                 OrbatStructure orbat, Optional<PostingDetails> currentActiveOpt) {
        log.info("Processing JOINING (TOS provided) for personnel ID: {}", personnel.getId());

        if (currentActiveOpt.isPresent()) {
            PostingDetails current = currentActiveOpt.get();
            log.info("Current active posting found with status: {}, ID: {}",
                    current.getStatus(), current.getPostingId());

            if (UNDER_POSTING.equals(current.getStatus())) {
                // UNDER_POSTING → POSTED
                log.info("Completing UNDER_POSTING ID: {} → POSTED", current.getPostingId());

                current.setStatus(POSTED);
                current.setTosUpdatedDate(dto.getTosUpdatedDate());
                current.setToDate(dto.getTosUpdatedDate());
                current.setUnitName(current.getPostedTo());

                // ✅ Current posting ki duration abhi "Present" rahegi
                current.setDuration("Present");

                if (dto.getRank() != null) {
                    current.setRank(dto.getRank());
                }

                return postingRepository.save(current);

            } else if (POSTED.equals(current.getStatus())) {
                log.info("Current status is POSTED. Creating new POSTED record");

                // ✅ FIXED: Calculate duration for previous posting using its tosUpdatedDate
                // and the new TOS date
                if (current.getTosUpdatedDate() != null && dto.getTosUpdatedDate() != null) {
                    String duration = calculateDuration(current.getTosUpdatedDate(), dto.getTosUpdatedDate());
                    current.setDuration(duration);
                    log.info("Duration calculated for previous posting ID {}: from {} to {} = {}",
                            current.getPostingId(), current.getTosUpdatedDate(), dto.getTosUpdatedDate(), duration);
                }

                current.setStatus(PREVIOUS_POSTING);
                postingRepository.save(current);

                PostingDetails newPosting = createNewPosting(dto, personnel, orbat);
                newPosting.setStatus(POSTED);
                newPosting.setTosUpdatedDate(dto.getTosUpdatedDate());
                newPosting.setToDate(dto.getTosUpdatedDate());
                newPosting.setDuration("Present"); // New posting ki duration "Present"
                newPosting.setUnitName(dto.getPostedTo());

                return postingRepository.save(newPosting);
            }
        }

        log.info("No current posting. Creating first POSTED record");

        PostingDetails newPosting = createNewPosting(dto, personnel, orbat);
        newPosting.setStatus(POSTED);
        newPosting.setTosUpdatedDate(dto.getTosUpdatedDate());
        newPosting.setToDate(dto.getTosUpdatedDate());
        newPosting.setDuration("Present"); // First posting ki duration "Present"
        newPosting.setUnitName(dto.getPostedTo());

        return postingRepository.save(newPosting);
    }

    // ==================== PROCESS UNDER POSTING (NO TOS) ====================
    private PostingDetails processUnderPosting(PostingRequestDTO dto, Personnel personnel,
                                               OrbatStructure orbat, Optional<PostingDetails> currentActiveOpt) {
        log.info("Processing UNDER_POSTING (no TOS) for personnel ID: {}", personnel.getId());

        if (currentActiveOpt.isPresent()) {
            PostingDetails current = currentActiveOpt.get();
            log.info("Current active posting found with status: {}, ID: {}",
                    current.getStatus(), current.getPostingId());

            if (UNDER_POSTING.equals(current.getStatus())) {
                log.info("Updating existing UNDER_POSTING ID: {}", current.getPostingId());
                updatePostingFields(current, dto, orbat);
                return postingRepository.save(current);

            } else if (POSTED.equals(current.getStatus())) {
                log.info("Creating new UNDER_POSTING for transfer");

                // ✅ Note: Jab naya UNDER_POSTING create ho raha hai, tab previous POSTED ki duration
                // calculate nahi karenge - ye tab hoga jab iska end date aayega (next TOS)
                log.info("Previous posting ID {} has tosUpdatedDate: {} - duration will be calculated later",
                        current.getPostingId(), current.getTosUpdatedDate());

                current.setStatus(PREVIOUS_POSTING);
                postingRepository.save(current);

                PostingDetails newPosting = createNewPosting(dto, personnel, orbat);
                newPosting.setStatus(UNDER_POSTING);
                newPosting.setDuration("Present");

                return postingRepository.save(newPosting);
            }
        }

        log.info("No current posting. Creating first UNDER_POSTING record");

        PostingDetails newPosting = createNewPosting(dto, personnel, orbat);
        newPosting.setStatus(UNDER_POSTING);
        newPosting.setDuration("Present");

        return postingRepository.save(newPosting);
    }

    // ==================== CREATE NEW POSTING ====================
    private PostingDetails createNewPosting(PostingRequestDTO dto, Personnel personnel, OrbatStructure orbat) {
        PostingDetails posting = new PostingDetails();

        posting.setPersonnelId(personnel.getId());

        posting.setMovementDate(dto.getMovementDate());
        posting.setPostedTo(dto.getPostedTo());
        posting.setOrbatId(dto.getOrbatId());
        posting.setAppointment(dto.getAppointment());
        posting.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());
        posting.setTypeOfPosting(dto.getTypeOfPosting());

        posting.setRank(dto.getRank());
        posting.setTosUpdatedDate(dto.getTosUpdatedDate());

        posting.setFormationType(orbat.getFormationType());
        posting.setLocation(orbat.getLocation());
        posting.setCommand(orbat.getCommandName());

        posting.setToDate(null);
        posting.setSosDate(null);
        posting.setFromDate(null);

        return posting;
    }

    // ==================== UPDATE FIELDS ====================
    private void updatePostingFields(PostingDetails posting, PostingRequestDTO dto, OrbatStructure orbat) {
        if (dto.getMovementDate() != null) {
            posting.setMovementDate(dto.getMovementDate());
        }

        if (dto.getPostedTo() != null && !dto.getPostedTo().equals(posting.getPostedTo())) {
            posting.setPostedTo(dto.getPostedTo());
            posting.setOrbatId(dto.getOrbatId());
            posting.setFormationType(orbat.getFormationType());
            posting.setLocation(orbat.getLocation());
            posting.setCommand(orbat.getCommandName());
        }

        if (dto.getAppointment() != null) {
            posting.setAppointment(dto.getAppointment());
        }

        if (dto.getPostingOrderIssueDate() != null) {
            posting.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());
        }

        if (dto.getTypeOfPosting() != null) {
            posting.setTypeOfPosting(dto.getTypeOfPosting());
        }

        if (dto.getRank() != null) {
            posting.setRank(dto.getRank());
        }
    }

    // ==================== EXISTING METHODS ====================

    @Override
    public Long validateAndGetOrbatId(String formationName) {
        OrbatStructure orbat = orbatRepository.findByFormationNameCaseInsensitive(formationName.trim())
                .orElseThrow(() -> new RuntimeException("Formation not found"));
        return orbat.getId();
    }

    @Override
    public PostingDetails getCurrentPosting(Long personnelId) {
        return getCurrentActivePosting(personnelId)
                .orElseThrow(() -> new RuntimeException("No current posting found"));
    }

    @Override
    public List<PostingDetails> getPersonnelPostings(Long personnelId) {
        return postingRepository.findByPersonnelIdOrderByTosUpdatedDateDesc(personnelId);
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

        getCurrentActivePosting(personnelId).ifPresent(current -> {
            summary.setPostingId(current.getPostingId());
            summary.setMovementDate(current.getMovementDate());
            summary.setPostedTo(current.getPostedTo());
            summary.setAppointment(current.getAppointment());
            summary.setPostingOrderIssueDate(current.getPostingOrderIssueDate());
            summary.setTypeOfPosting(current.getTypeOfPosting());
            summary.setTosUpdatedDate(current.getTosUpdatedDate());
            summary.setStatus(current.getStatus());
            summary.setOrbatId(current.getOrbatId());
            summary.setUnitName(current.getUnitName());
        });

        return summary;
    }

    @Override
    @Transactional
    public void deletePosting(Long postingId) {
        PostingDetails posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new RuntimeException("Posting not found"));

        if (UNDER_POSTING.equals(posting.getStatus())) {
            throw new RuntimeException("Cannot delete current posting");
        }

        postingRepository.delete(posting);
    }

    public List<PostingDetails> getPersonnelPostingsByArmyNo(String armyNo) {
        Personnel personnel = personnelRepository.findByArmyNo(armyNo)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));
        return getPersonnelPostings(personnel.getId());
    }

    public PostingDetails getCurrentPostingByArmyNo(String armyNo) {
        Personnel personnel = personnelRepository.findByArmyNo(armyNo)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));
        return getCurrentPosting(personnel.getId());
    }

    @Override
    public Optional<PostingDetails> getCurrentActivePosting(Long personnelId) {

        log.debug("Getting current active posting for personnel ID: {}", personnelId);

        // Priority 1: UNDER_POSTING
        Optional<PostingDetails> underPosting = postingRepository.findCurrentUnderPosting(personnelId);
        if (underPosting.isPresent()) {
            log.debug("Found UNDER_POSTING with ID: {}", underPosting.get().getPostingId());
            return underPosting;
        }

        // Priority 2: Latest POSTED
        Optional<PostingDetails> latestPosted = postingRepository.findLatestPosted(personnelId);
        if (latestPosted.isPresent()) {
            log.debug("Found latest POSTED with ID: {}", latestPosted.get().getPostingId());
            return latestPosted;
        }

        log.debug("No active posting found for personnel ID: {}", personnelId);
        return Optional.empty();
    }

    /**
     * Get current posting details as DTO for frontend
     */
    @Override
    public PostingResponseDTO getCurrentPostingDetails(Long personnelId) {
        Optional<PostingDetails> currentOpt = getCurrentActivePosting(personnelId);

        if (currentOpt.isEmpty()) {
            return null;
        }

        PostingDetails current = currentOpt.get();
        PostingResponseDTO dto = new PostingResponseDTO();

        dto.setPostingId(current.getPostingId());
        dto.setPersonnelId(current.getPersonnelId());
        dto.setMovementDate(current.getMovementDate());
        dto.setPostedTo(current.getPostedTo());
        dto.setAppointment(current.getAppointment());
        dto.setPostingOrderIssueDate(current.getPostingOrderIssueDate());
        dto.setTypeOfPosting(current.getTypeOfPosting());
        dto.setTosUpdatedDate(current.getTosUpdatedDate());
        dto.setRank(current.getRank());
        dto.setStatus(current.getStatus());
        dto.setOrbatId(current.getOrbatId());
        dto.setUnitName(current.getUnitName());
        dto.setFormationType(current.getFormationType());
        dto.setLocation(current.getLocation());

        personnelRepository.findById(current.getPersonnelId()).ifPresent(personnel -> {
            dto.setArmyNo(personnel.getArmyNo());
            dto.setPersonnelName(personnel.getFullName());
        });

        return dto;
    }

    @Override
    public List<PostingHistoryDTO> getPostingHistory(Long personnelId) {
        List<PostingDetails> historyList = postingRepository.findPostingHistory(personnelId);

        return historyList.stream()
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    private PostingHistoryDTO convertToHistoryDTO(PostingDetails posting) {
        PostingHistoryDTO dto = new PostingHistoryDTO();
        dto.setPostingId(posting.getPostingId());
        dto.setUnitName(posting.getPostedTo());
        dto.setAppointment(posting.getAppointment());
        dto.setTypeOfPosting(posting.getTypeOfPosting());
        dto.setRank(posting.getRank());
        dto.setDuration(posting.getDuration() != null ? posting.getDuration() : "-");
        dto.setStatus(posting.getStatus());
        return dto;
    }

    // ==================== DURATION CALCULATION ====================
    private String calculateDuration(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            return "-";
        }

        Period period = Period.between(fromDate, toDate);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        StringBuilder duration = new StringBuilder();
        if (years > 0) {
            duration.append(years).append(" yr ");
        }
        if (months > 0) {
            duration.append(months).append(" m ");
        }
        if (days > 0) {
            duration.append(days).append(" d");
        }

        String result = duration.toString().trim();
        return result.isEmpty() ? "< 1 d" : result;
    }
}