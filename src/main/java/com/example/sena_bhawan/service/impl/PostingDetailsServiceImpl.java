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

        Personnel personnel = personnelRepository.findByArmyNo(dto.getArmyNo())
                .orElseThrow(() -> new RuntimeException(
                        "Personnel not found with Army No: " + dto.getArmyNo()));

        log.info("Processing posting for Army No: {}, Personnel ID: {}",
                personnel.getArmyNo(), personnel.getId());

        dto.setPersonnelId(personnel.getId());

        if (dto.getPostedTo() == null || dto.getPostedTo().trim().isEmpty()) {
            throw new RuntimeException("Posted To field cannot be empty");
        }

        if (dto.getOrbatId() == null) {
            throw new RuntimeException("Invalid formation selection. Please select from recommendations.");
        }

        OrbatStructure orbat = orbatRepository.findById(dto.getOrbatId())
                .orElseThrow(() -> new RuntimeException("Invalid ORBAT ID"));

        Optional<PostingDetails> currentActiveOpt = getCurrentActivePosting(personnel.getId());

        if (dto.getTosUpdatedDate() != null) {
            return processJoiningPosting(dto, personnel, orbat, currentActiveOpt);
        } else {
            return processUnderPosting(dto, personnel, orbat, currentActiveOpt);
        }
    }

    // ==================== PROCESS JOINING (WITH TOS) - FINAL ====================
    private PostingDetails processJoiningPosting(PostingRequestDTO dto, Personnel personnel,
                                                 OrbatStructure orbat, Optional<PostingDetails> currentActiveOpt) {
        log.info("Processing JOINING (TOS provided) for personnel ID: {}", personnel.getId());

        // CASE 1: Current UNDER_POSTING exists
        if (currentActiveOpt.isPresent() && UNDER_POSTING.equals(currentActiveOpt.get().getStatus())) {
            PostingDetails current = currentActiveOpt.get();
            log.info("Found UNDER_POSTING ID: {} → Converting to POSTED", current.getPostingId());

            // ✅ Find the previous POSTED record (jo abhi POSTED hai)
            Optional<PostingDetails> previousPosted = postingRepository
                    .findLatestPostedBeforeId(personnel.getId(), current.getPostingId());

            if (previousPosted.isPresent()) {
                PostingDetails previous = previousPosted.get();
                log.info("Found previous POSTED ID: {} with unit_name: {}, TOS: {}",
                        previous.getPostingId(), previous.getUnitName(), previous.getTosUpdatedDate());

                // ✅ Calculate duration using its TOS date and new TOS date
                if (previous.getTosUpdatedDate() != null && dto.getTosUpdatedDate() != null) {
                    String duration = calculateDuration(previous.getTosUpdatedDate(), dto.getTosUpdatedDate());
                    previous.setDuration(duration);
                    log.info("✅ Duration calculated for previous ID {}: {} to {} = {}",
                            previous.getPostingId(), previous.getTosUpdatedDate(),
                            dto.getTosUpdatedDate(), duration);
                }

                // ✅ Previous POSTED → PREVIOUS_POSTING
                previous.setStatus(PREVIOUS_POSTING);

                // ✅ Set its posted_to to its unit_name (where they served)
                previous.setPostedTo(previous.getUnitName());

                log.info("✅ Previous ID {} → PREVIOUS_POSTING, posted_to set to: {}",
                        previous.getPostingId(), previous.getUnitName());
                postingRepository.save(previous);
            }

            // ✅ Update current posting to POSTED
            current.setStatus(POSTED);
            current.setTosUpdatedDate(dto.getTosUpdatedDate());
            current.setToDate(dto.getTosUpdatedDate());

            // ✅ unit_name = posted_to (new unit), posted_to = null
            current.setUnitName(current.getPostedTo());
            current.setPostedTo(null);
            current.setDuration("Present");

            // ✅ Rank logic
            String rankToSet = (dto.getRank() != null) ? dto.getRank() : personnel.getRank();
            current.setRank(rankToSet);

            if (dto.getRank() != null && !dto.getRank().equals(personnel.getRank())) {
                personnel.setRank(dto.getRank());
                personnelRepository.save(personnel);
                log.info("✅ Rank updated in personnel table: {} → {}",
                        personnel.getArmyNo(), dto.getRank());
            }

            return postingRepository.save(current);
        }

        // CASE 2: No UNDER_POSTING exists (first time OR UNDER_POSTING + TOS together)
        log.info("No UNDER_POSTING found. Creating new POSTED record...");

        Optional<PostingDetails> latestPosted = postingRepository.findLatestPosted(personnel.getId());

        if (latestPosted.isPresent()) {
            PostingDetails previous = latestPosted.get();
            log.info("Found latest POSTED ID: {} with unit_name: {}, TOS: {}",
                    previous.getPostingId(), previous.getUnitName(), previous.getTosUpdatedDate());

            // ✅ Calculate duration using its TOS date and new TOS date
            if (previous.getTosUpdatedDate() != null && dto.getTosUpdatedDate() != null) {
                String duration = calculateDuration(previous.getTosUpdatedDate(), dto.getTosUpdatedDate());
                previous.setDuration(duration);
                log.info("✅ Duration calculated for previous ID {}: {} to {} = {}",
                        previous.getPostingId(), previous.getTosUpdatedDate(),
                        dto.getTosUpdatedDate(), duration);
            }

            // ✅ Previous POSTED → PREVIOUS_POSTING
            previous.setStatus(PREVIOUS_POSTING);

            // ✅ Set its posted_to to its unit_name (where they served)
            previous.setPostedTo(previous.getUnitName());

            log.info("✅ Previous ID {} → PREVIOUS_POSTING, posted_to set to: {}",
                    previous.getPostingId(), previous.getUnitName());
            postingRepository.save(previous);
        }

        // ✅ Create new POSTED record
        PostingDetails newPosting = createNewPosting(dto, personnel, orbat);
        newPosting.setStatus(POSTED);
        newPosting.setTosUpdatedDate(dto.getTosUpdatedDate());
        newPosting.setToDate(dto.getTosUpdatedDate());

        // ✅ unit_name = posted_to (new unit), posted_to = null
        newPosting.setUnitName(newPosting.getPostedTo());
        newPosting.setPostedTo(null);
        newPosting.setDuration("Present");

        // ✅ Rank logic
        String rankToSet = (dto.getRank() != null) ? dto.getRank() : personnel.getRank();
        newPosting.setRank(rankToSet);

        if (dto.getRank() != null && !dto.getRank().equals(personnel.getRank())) {
            personnel.setRank(dto.getRank());
            personnelRepository.save(personnel);
            log.info("✅ Rank updated in personnel table: {} → {}",
                    personnel.getArmyNo(), dto.getRank());
        }

        return postingRepository.save(newPosting);
    }

    // ==================== PROCESS UNDER POSTING (NO TOS) - FINAL ====================
    private PostingDetails processUnderPosting(PostingRequestDTO dto, Personnel personnel,
                                               OrbatStructure orbat, Optional<PostingDetails> currentActiveOpt) {
        log.info("Processing UNDER_POSTING (no TOS) for personnel ID: {}", personnel.getId());

        // CASE 1: Update existing UNDER_POSTING
        if (currentActiveOpt.isPresent() && UNDER_POSTING.equals(currentActiveOpt.get().getStatus())) {
            PostingDetails current = currentActiveOpt.get();
            log.info("Updating existing UNDER_POSTING ID: {}", current.getPostingId());
            updatePostingFields(current, dto, orbat);

            String rankToSet = (dto.getRank() != null) ? dto.getRank() : personnel.getRank();
            current.setRank(rankToSet);
            log.info("📝 Rank set in posting: {}", rankToSet);

            return postingRepository.save(current);
        }

        // CASE 2: Create new UNDER_POSTING (transfer)
        if (currentActiveOpt.isPresent() && POSTED.equals(currentActiveOpt.get().getStatus())) {

            PostingDetails current = currentActiveOpt.get();
            log.info("Creating new UNDER_POSTING for transfer");

            // ✅ Previous POSTED remains POSTED (duration will be calculated later)
            // ✅ Just update its posted_to to new unit
            current.setPostedTo(dto.getPostedTo());

            log.info("✅ Previous ID {} remains POSTED, posted_to set to: {}",
                    current.getPostingId(), dto.getPostedTo());
            postingRepository.save(current);
        }

        // ✅ Create new UNDER_POSTING
        PostingDetails newPosting = createNewPosting(dto, personnel, orbat);
        newPosting.setStatus(UNDER_POSTING);
        newPosting.setDuration("Present");

        // ✅ unit_name fix - set to previous unit's unit_name
        if (currentActiveOpt.isPresent()) {
            newPosting.setUnitName(currentActiveOpt.get().getUnitName());
            log.info("📝 New UNDER_POSTING unit_name set to: {}", currentActiveOpt.get().getUnitName());
        }

        String rankToSet = (dto.getRank() != null) ? dto.getRank() : personnel.getRank();
        newPosting.setRank(rankToSet);
        log.info("📝 New UNDER_POSTING created: unit_name={}, posted_to={}, rank={}",
                newPosting.getUnitName(), newPosting.getPostedTo(), rankToSet);

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

        if (dto.getTosUpdatedDate() != null) {
            posting.setTosUpdatedDate(dto.getTosUpdatedDate());
            posting.setToDate(dto.getTosUpdatedDate());
        }

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
        if (dto.getMovementDate() != null) posting.setMovementDate(dto.getMovementDate());
        if (dto.getPostedTo() != null && !dto.getPostedTo().equals(posting.getPostedTo())) {
            posting.setPostedTo(dto.getPostedTo());
            posting.setOrbatId(dto.getOrbatId());
            posting.setFormationType(orbat.getFormationType());
            posting.setLocation(orbat.getLocation());
            posting.setCommand(orbat.getCommandName());
        }
        if (dto.getAppointment() != null) posting.setAppointment(dto.getAppointment());
        if (dto.getPostingOrderIssueDate() != null) posting.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());
        if (dto.getTypeOfPosting() != null) posting.setTypeOfPosting(dto.getTypeOfPosting());
        if (dto.getTosUpdatedDate() != null) {
            posting.setTosUpdatedDate(dto.getTosUpdatedDate());
            posting.setToDate(dto.getTosUpdatedDate());
        }
    }

    // ==================== EXISTING METHODS ====================

    @Override
    public Long validateAndGetOrbatId(String formationName) {
        return orbatRepository.findByFormationNameCaseInsensitive(formationName.trim())
                .orElseThrow(() -> new RuntimeException("Formation not found")).getId();
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

        Optional<PostingDetails> underPosting = postingRepository.findCurrentUnderPosting(personnelId);
        if (underPosting.isPresent()) {
            log.debug("Found UNDER_POSTING with ID: {}", underPosting.get().getPostingId());
            return underPosting;
        }

        Optional<PostingDetails> latestPosted = postingRepository.findLatestPosted(personnelId);
        if (latestPosted.isPresent()) {
            log.debug("Found latest POSTED with ID: {}", latestPosted.get().getPostingId());
            return latestPosted;
        }

        log.debug("No active posting found for personnel ID: {}", personnelId);
        return Optional.empty();
    }

    @Override
    public PostingResponseDTO getCurrentPostingDetails(Long personnelId) {
        Optional<PostingDetails> currentOpt = getCurrentActivePosting(personnelId);

        if (currentOpt.isEmpty()) return null;

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
        dto.setUnitName(posting.getUnitName());
        dto.setAppointment(posting.getAppointment());
        dto.setTypeOfPosting(posting.getTypeOfPosting());
        dto.setRank(posting.getRank());
        dto.setDuration(posting.getDuration() != null ? posting.getDuration() : "-");
        dto.setStatus(posting.getStatus());
        dto.setTosUpdatedDate(posting.getTosUpdatedDate());
        return dto;
    }

    // ==================== DURATION CALCULATION ====================
    private String calculateDuration(LocalDate startTosDate, LocalDate endTosDate) {
        if (startTosDate == null || endTosDate == null) {
            return "-";
        }

        Period period = Period.between(startTosDate, endTosDate);
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

    // ==================== CANCEL UNDER POSTING ====================
    @Override
    @Transactional
    public PostingDetails cancelUnderPostingByPersonnelId(Long personnelId) {
        log.info("Cancelling UNDER_POSTING for personnel ID: {}", personnelId);

        Optional<PostingDetails> underPostingOpt = postingRepository
                .findCurrentUnderPosting(personnelId);

        if (underPostingOpt.isEmpty()) {
            throw new RuntimeException("No UNDER_POSTING found for personnel ID: " + personnelId);
        }

        PostingDetails underPosting = underPostingOpt.get();
        Long underPostingId = underPosting.getPostingId();

        log.info("Found UNDER_POSTING ID: {} with posted_to: {}",
                underPostingId, underPosting.getPostedTo());

        Optional<PostingDetails> previousPostingOpt = postingRepository
                .findLatestPreviousPostingBeforeId(personnelId, underPostingId);

        if (previousPostingOpt.isEmpty()) {
            throw new RuntimeException("No previous PREVIOUS_POSTING record found to restore.");
        }

        PostingDetails previousPosting = previousPostingOpt.get();
        log.info("Found previous PREVIOUS_POSTING ID: {} with unit_name: {}",
                previousPosting.getPostingId(), previousPosting.getUnitName());

        // ✅ Restore previous record to POSTED
        previousPosting.setStatus(POSTED);
        previousPosting.setTosUpdatedDate(null);
        previousPosting.setPostedTo(null);
        previousPosting.setDuration("Present");

        postingRepository.save(previousPosting);
        postingRepository.delete(underPosting);

        log.info("✅ Deleted UNDER_POSTING ID: {}, restored PREVIOUS_POSTING ID: {} as POSTED",
                underPostingId, previousPosting.getPostingId());

        return previousPosting;
    }
}