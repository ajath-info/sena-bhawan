package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.DecorationMaster;
import java.util.List;

public interface DecorationMasterService {

    DecorationMaster addDecoration(DecorationMaster decoration);

    DecorationMaster updateDecoration(Long id, DecorationMaster decoration);

    void deleteDecoration(Long id);

    DecorationMaster getDecorationById(Long id);

    List<DecorationMaster> getAllDecorations();

    List<Object> getDecorationDropdown();

    List<String> getAllCategories();

    List<Object> getAwardsByCategory(String category);

}
