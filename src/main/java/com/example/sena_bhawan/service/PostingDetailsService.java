package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.PostingDetailsDTO;
import com.example.sena_bhawan.entity.PostingDetails;

import java.util.List;

public interface PostingDetailsService {

    PostingDetails addPosting(PostingDetailsDTO dto);

    List<PostingDetails> getByPersonnel(Long personnelId);

    PostingDetails updatePosting(Long postingId, PostingDetailsDTO dto);

    void deletePosting(Long postingId);
}
