package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.MedicalUpdateRequestDTO;
import com.example.sena_bhawan.entity.MedicalCategoryHistory;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.repository.MedicalCategoryHistoryRepository;
import com.example.sena_bhawan.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonnelMedicalService {

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private MedicalCategoryHistoryRepository historyRepository;

    public void updateMedical(MedicalUpdateRequestDTO dto) {

        // 1️⃣ Personnel lao
        Personnel p = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        // 2️⃣ Old value nikaalo (from medical_category)
        int oldValue = getOldValue(p.getMedicalCategory(), dto.getCategory());

        // 3️⃣ Current medical update (personnel table)
        String updatedCategory = updateMedicalCategory(
                p.getMedicalCategory(),
                dto.getCategory(),
                dto.getNewValue()
        );

        p.setMedicalCategory(updatedCategory);
        p.setMedicalDate(dto.getChangeDate());
        p.setDiagnosis(dto.getDiagnosis());
        p.setReviewDate(dto.getNextReviewDate());
        p.setRestriction(dto.getRestriction());

        personnelRepository.save(p);

        // 4️⃣ HISTORY INSERT (sirf jab 1 → change ho)
        if (oldValue == 1 && dto.getNewValue() != 1) {

            MedicalCategoryHistory h = new MedicalCategoryHistory();
            h.setPersonnelId(p.getId());
            h.setCategory(dto.getCategory());
            h.setOldValue(oldValue);
            h.setNewValue(dto.getNewValue());
            h.setChangeDate(dto.getChangeDate());
            h.setDiagnosis(dto.getDiagnosis());
            h.setNextReviewDate(dto.getNextReviewDate());
            h.setCategoryType(dto.getCategoryType());
            h.setRestriction(dto.getRestriction());

            historyRepository.save(h);
        }
    }

    // ================= HELPERS =================

    private int getOldValue(String medicalCategory, String category) {
        // Example: S1H1A1P1E1
        int index = medicalCategory.indexOf(category);
        return Integer.parseInt(
                String.valueOf(medicalCategory.charAt(index + 1))
        );
    }

    private String updateMedicalCategory(String medicalCategory,
                                         String category,
                                         int newValue) {

        char[] chars = medicalCategory.toCharArray();
        int index = medicalCategory.indexOf(category);
        chars[index + 1] = Character.forDigit(newValue, 10);
        return new String(chars);
    }
}
