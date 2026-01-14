package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.PostingDetailsDTO;
import com.example.sena_bhawan.entity.PostingDetails;
import com.example.sena_bhawan.repository.PostingDetailsRepository;
import com.example.sena_bhawan.service.PostingDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostingDetailsServiceImpl implements PostingDetailsService {

    private final PostingDetailsRepository repo;

    @Override
    public PostingDetails addPosting(PostingDetailsDTO dto) {

        PostingDetails pd = new PostingDetails();
        pd.setPersonnelId(dto.getPersonnelId());
        pd.setUnitName(dto.getUnitName());
        pd.setLocation(dto.getLocation());
        pd.setCommand(dto.getCommand());
        pd.setAppointment(dto.getAppointment());
        pd.setFromDate(dto.getFromDate());
        pd.setToDate(dto.getToDate());
        pd.setDuration(dto.getDuration());
        pd.setRemarks(dto.getRemarks());
        pd.setDocumentPath(dto.getDocumentPath());

        // Auto-calc duration
        if (dto.getToDate() != null) {
            Period p = Period.between(dto.getFromDate(), dto.getToDate());
            pd.setDuration(p.getYears() + " Years " + p.getMonths() + " Months");
        } else {
            pd.setDuration("Present");
        }

        return repo.save(pd);
    }

    @Override
    public List<PostingDetails> getByPersonnel(Long personnelId) {
        return repo.findByPersonnelId(personnelId);
    }

    @Override
    public PostingDetails updatePosting(Long postingId, PostingDetailsDTO dto) {
        PostingDetails pd = repo.findById(postingId).orElseThrow();

        pd.setUnitName(dto.getUnitName());
        pd.setLocation(dto.getLocation());
        pd.setCommand(dto.getCommand());
        pd.setAppointment(dto.getAppointment());
        pd.setFromDate(dto.getFromDate());
        pd.setToDate(dto.getToDate());
        pd.setRemarks(dto.getRemarks());
        pd.setDocumentPath(dto.getDocumentPath());

        // Recalculate
        if (dto.getToDate() != null) {
            Period p = Period.between(dto.getFromDate(), dto.getToDate());
            pd.setDuration(p.getYears() + " Years " + p.getMonths() + " Months");
        } else {
            pd.setDuration("Present");
        }

        return repo.save(pd);
    }

    @Override
    public void deletePosting(Long postingId) {
        repo.deleteById(postingId);
    }
}
