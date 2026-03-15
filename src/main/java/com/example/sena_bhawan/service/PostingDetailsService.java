package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.PostingDetailsDTO;
import com.example.sena_bhawan.dto.PostingHistoryDTO;
import com.example.sena_bhawan.dto.PostingRequestDTO;
import com.example.sena_bhawan.dto.PostingResponseDTO;
import com.example.sena_bhawan.entity.PostingDetails;

import java.util.List;
import java.util.Optional;

public interface PostingDetailsService {
    Optional<PostingDetails> getCurrentActivePosting(Long personnelId);
    PostingResponseDTO getCurrentPostingDetails(Long personnelId);
    List<PostingHistoryDTO> getPostingHistory(Long personnelId);
    List<PostingDetails> getByPersonnel(Long personnelId);


    // Main method to handle both save and update
    PostingDetails upsertPosting(PostingRequestDTO dto);

    // Get current posting by Army Number
    PostingDetails getCurrentPostingByArmyNo(String armyNo);

    // Get all postings by Army Number
    List<PostingDetails> getPersonnelPostingsByArmyNo(String armyNo);

    // Get current posting for a personnel
    PostingDetails getCurrentPosting(Long personnelId);

    // Get all postings (history) for a personnel
    List<PostingDetails> getPersonnelPostings(Long personnelId);

    // Get posting history with response DTO
    PostingResponseDTO getPersonnelPostingSummary(Long personnelId);

    // Validate formation against ORBAT
    Long validateAndGetOrbatId(String formationName);

    // Delete posting (only if not current)
    void deletePosting(Long postingId);
}
