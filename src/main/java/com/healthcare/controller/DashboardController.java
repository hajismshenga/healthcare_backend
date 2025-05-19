package com.healthcare.controller;

import com.healthcare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private PatientRepository patientRepo;
    @Autowired private DoctorRepository doctorRepo;
    @Autowired private PrescriptionRepository prescriptionRepo;
    @Autowired private SecondOpinionRequestRepository opinionRepo;
    @Autowired private LabTestRepository labTestRepo;
    @Autowired private LabResultRepository labResultRepo;

    @GetMapping("/summary")
    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalPatients", patientRepo.count());
        summary.put("totalDoctors", doctorRepo.count());
        summary.put("totalPrescriptions", prescriptionRepo.count());
        summary.put("totalSecondOpinions", opinionRepo.count());
        summary.put("totalLabTests", labTestRepo.count());
        summary.put("totalLabResults", labResultRepo.count());
        return summary;
    }
}
