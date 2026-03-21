package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.RemarksHistoryDTO;
import com.example.sena_bhawan.dto.RemarksUpdateDTO;
import com.example.sena_bhawan.entity.RemarksUpdate;
import com.example.sena_bhawan.service.RemarksUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/remarks")
public class RemarksUpdateController {

    @Autowired
    private RemarksUpdateService remarksUpdateService;

    @PostMapping("/save")
    public ResponseEntity<?> saveRemarks(@RequestBody RemarksUpdateDTO dto) {
        RemarksUpdate saved = remarksUpdateService.saveOrUpdate(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", saved);
        response.put("message", "Remarks saved successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{personnelId}")
    public ResponseEntity<?> getRemarksHistory(@PathVariable Long personnelId) {
        List<RemarksHistoryDTO> history = remarksUpdateService.getRemarksHistory(personnelId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", history);

        return ResponseEntity.ok(response);
    }
}