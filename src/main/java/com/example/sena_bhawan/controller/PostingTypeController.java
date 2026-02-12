package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.PostingTypeMaster;
import com.example.sena_bhawan.service.PostingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postingtypes")
@RequiredArgsConstructor
public class PostingTypeController {

    private final PostingTypeService service;

    @GetMapping
    public ResponseEntity<List<PostingTypeMaster>> getAllPostingTypes() {
        return ResponseEntity.ok(service.getAllPostingTypes());
    }
}
