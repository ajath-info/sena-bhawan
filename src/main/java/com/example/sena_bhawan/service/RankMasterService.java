package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.RankMaster;
import com.example.sena_bhawan.projection.RankOnly;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.repository.RankMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RankMasterService {

    @Autowired
    private PersonnelRepository personnelRepo;

    @Autowired
    private RankMasterRepository rankMasterRepo;

    @Transactional
    public List<RankMaster> refreshAndGetRankMaster() {

        // Get counts from personnel
        List<Object[]> results = personnelRepo.getRankCounts();

        for (Object[] row : results) {
            String rank = (String) row[0];
            Long count = (Long) row[1];

            // find existing rank by name
            RankMaster existing = rankMasterRepo.findByRank(rank);

            if (existing == null) {
                // create new rank
                RankMaster rm = new RankMaster();
                rm.setRank(rank);
                rm.setPersonnelCount(count.intValue());
                rm.setUpdatedAt(LocalDateTime.now());
                rankMasterRepo.save(rm);
            } else {
                // update existing
                existing.setPersonnelCount(count.intValue());
                existing.setUpdatedAt(LocalDateTime.now());
                rankMasterRepo.save(existing);
            }
        }

        return rankMasterRepo.findAll();
    }


    public List<String> getAllRanks() {
        return rankMasterRepo.findAllBy()
                .stream()
                .map(RankOnly::getRank)
                .toList();
    }
}

