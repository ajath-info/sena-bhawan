package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.PostingDetailsDTO;
import com.example.sena_bhawan.dto.PostingRequestDTO;
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
    public PostingDetails addPosting(PostingRequestDTO dto) {

        PostingDetails pd = new PostingDetails();

        pd.setPersonnelId(dto.getPersonnelId());

        // Under Posting
        pd.setMovementDate(dto.getMovementDate());
        pd.setPostedTo(dto.getPostedTo());
        pd.setAppointment(dto.getAppointment());
        pd.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());

        // Posting In
        pd.setTosUpdatedDate(dto.getTosUpdatedDate());
        pd.setRank(dto.getRank());

        // 🔥 STATUS LOGIC
        if (dto.getTosUpdatedDate() != null) {
            pd.setStatus("POSTED");
        } else {
            pd.setStatus("UNDER_POSTING");
        }

        return repo.save(pd);
    }

//    @Override
//    public PostingDetails addPosting(PostingDetailsDTO dto) {
//
//        PostingDetails pd = new PostingDetails();
//        pd.setPersonnelId(dto.getPersonnelId());
//        pd.setUnitName(dto.getUnitName());
//        pd.setLocation(dto.getLocation());
//        pd.setCommand(dto.getCommand());
//        pd.setAppointment(dto.getAppointment());
//        pd.setFromDate(dto.getFromDate());
//        pd.setToDate(dto.getToDate());
//        pd.setDuration(dto.getDuration());
//        pd.setRemarks(dto.getRemarks());
//        pd.setDocumentPath(dto.getDocumentPath());
//        pd.setMovementDate(dto.getMovementDate());
//        pd.setPostedTo(dto.getPostedTo());
//        pd.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());
//        pd.setTosUpdatedDate(dto.getTosUpdatedDate());
//        pd.setRank(dto.getRank());
//
//        // Auto-calc duration
//        if (dto.getToDate() != null) {
//            Period p = Period.between(dto.getFromDate(), dto.getToDate());
//            pd.setDuration(p.getYears() + " Years " + p.getMonths() + " Months");
//        } else {
//            pd.setDuration("Present");
//        }
//
//        return repo.save(pd);
//    }

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

    // PostingDetailsServiceImpl mein implement karo
    @Override
    public PostingDetails upsertPosting(PostingRequestDTO dto) {

        // Pehle check karo ki personnel ki already posting hai ya nahi
        List<PostingDetails> existingPostings = repo.findByPersonnelId(dto.getPersonnelId());

        if (!existingPostings.isEmpty()) {
            // UPDATE: Pehli posting ko update karo (ya specific logic)
            PostingDetails existing = existingPostings.get(0); // ya koi specific condition

            // Update fields
            existing.setMovementDate(dto.getMovementDate());
            existing.setPostedTo(dto.getPostedTo());
            existing.setAppointment(dto.getAppointment());
            existing.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());
            existing.setTosUpdatedDate(dto.getTosUpdatedDate());
            existing.setRank(dto.getRank());

            // Status update based on TOS date
            if (dto.getTosUpdatedDate() != null) {
                existing.setStatus("POSTED");
            } else {
                existing.setStatus("UNDER_POSTING");
            }

            return repo.save(existing);
        } else {
            // INSERT: Naya record
            PostingDetails pd = new PostingDetails();
            pd.setPersonnelId(dto.getPersonnelId());
            pd.setMovementDate(dto.getMovementDate());
            pd.setPostedTo(dto.getPostedTo());
            pd.setAppointment(dto.getAppointment());
            pd.setPostingOrderIssueDate(dto.getPostingOrderIssueDate());
            pd.setTosUpdatedDate(dto.getTosUpdatedDate());
            pd.setRank(dto.getRank());

            // Status logic
            if (dto.getTosUpdatedDate() != null) {
                pd.setStatus("POSTED");
            } else {
                pd.setStatus("UNDER_POSTING");
            }

            return repo.save(pd);
        }
    }

    @Override
    public void deletePosting(Long postingId) {
        repo.deleteById(postingId);
    }
}
