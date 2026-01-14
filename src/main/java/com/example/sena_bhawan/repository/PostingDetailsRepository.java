package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PostingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostingDetailsRepository extends JpaRepository<PostingDetails, Long> {

    List<PostingDetails> findByPersonnelId(Long personnelId);
}
