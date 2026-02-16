package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.RemarksUpdateDTO;
import com.example.sena_bhawan.entity.RemarksUpdate;
import com.example.sena_bhawan.service.RemarksUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/remarks")
public class RemarksUpdateController {

    @Autowired
    RemarksUpdateService remarksUpdateService;

    @PostMapping("/save")
    public ResponseEntity<?> saveRemarks(
            @RequestBody RemarksUpdateDTO dto) {

        RemarksUpdate saved = remarksUpdateService.saveOrUpdate(dto);
        return ResponseEntity.ok(saved);
    }
}
