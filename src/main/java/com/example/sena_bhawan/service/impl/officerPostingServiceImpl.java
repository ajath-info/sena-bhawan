package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.OfficerPostingRequestDTO;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.service.OfficerPostingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class officerPostingServiceImpl implements OfficerPostingService {

    private final OfficerPostingRepository officerPostingRepository;
    private final PersonnelRepository personnelRepository;

    @Override
    public OfficerPosting createPosting(OfficerPostingRequestDTO request) {

        // 🛑 HARD STOP if ID missing
        if (request.getPersonnelId() == null) {
            throw new IllegalArgumentException("personnelId must not be null");
        }

        Personnel personnel = personnelRepository.findById(
                request.getPersonnelId()
        ).orElseThrow(() ->
                new RuntimeException("Personnel not found with id " + request.getPersonnelId())
        );

        OfficerPosting posting = new OfficerPosting();
        posting.setOfficerArmyNo(request.getOfficerArmyNo());
        posting.setOfficerName(request.getOfficerName());
        posting.setMovementDate(request.getMovementDate());
        posting.setPostedToUnit(request.getPostedToUnit());
        posting.setAppointment(request.getAppointment());
        posting.setPostingOrderIssueDate(request.getPostingOrderIssueDate());
        posting.setPostingType(request.getPostingType());
        posting.setTosUpdateDate(request.getTosUpdateDate());
        posting.setRankOnPromotion(request.getRankOnPromotion());

        posting.setPersonnel(personnel); // 🔥 FK set properly

        return officerPostingRepository.save(posting);
    }

    @Override
    public OfficerPosting createPosting(OfficerPosting posting) {
        return null;
    }





//    @Transactional
//    @Override
//    public OfficerPosting createPosting(OfficerPosting posting) {
//
//        if (posting.getPersonnel() == null || posting.getPersonnel().getId() == null) {
//            throw new IllegalArgumentException("Personnel ID is required");
//        }
//
//        Personnel personnel = personnelRepository.findById(
//                posting.getPersonnel().getId()
//        ).orElseThrow(() ->
//                new RuntimeException("Personnel not found")
//        );
//
//        posting.setOfficerArmyNo(personnel.getArmyNo());
//        posting.setOfficerName(personnel.getFullName());
//        posting.setPersonnel(personnel);
//
//        return officerPostingRepository.save(posting);
//    }

}
