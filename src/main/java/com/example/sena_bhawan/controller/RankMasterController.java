package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.entity.RankMaster;
import com.example.sena_bhawan.service.RankMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranks")
public class RankMasterController {

    @Autowired
    private RankMasterService service;

    @GetMapping("/master")
    public List<RankMaster> getRankMasterList() {
        return service.refreshAndGetRankMaster();
    }
}

