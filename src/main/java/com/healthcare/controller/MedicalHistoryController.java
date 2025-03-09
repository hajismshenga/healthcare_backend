package com.healthcare.controller;

import com.healthcare.model.MedicalHistory;
import com.healthcare.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medical-history")
public class MedicalHistoryController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    // Create medical history linked to an appointment
    @PostMapping("/{appointmentId}")
    public MedicalHistory createMedicalHistory(@PathVariable Long appointmentId,
                                               @RequestBody MedicalHistory medicalHistory) {
        return medicalHistoryService.createMedicalHistory(appointmentId, medicalHistory);
    }
}
