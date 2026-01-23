package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.PostingTypeMaster;
import com.example.sena_bhawan.repository.PostingTypeMasterRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostingTypeService {

    private final PostingTypeMasterRepo repo;

    public List<PostingTypeMaster> getAllPostingTypes() {
        return repo.findAll();
    }
}
