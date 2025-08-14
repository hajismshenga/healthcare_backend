package com.healthcare.controller;

import com.healthcare.dto.PatientMedicationHistoryResponse;
import com.healthcare.service.PatientMedicationHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/patient-medications")
public class PatientMedicationHistoryController {
    
    private final PatientMedicationHistoryService patientMedicationHistoryService;

    public PatientMedicationHistoryController(PatientMedicationHistoryService patientMedicationHistoryService) {
        this.patientMedicationHistoryService = patientMedicationHistoryService;
        log.info("PatientMedicationHistoryController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("PatientMedicationHistoryController is working!");
    }

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<PatientMedicationHistoryResponse>> getPatientMedicationHistory(
            @PathVariable String patientId) {
        log.info("Fetching medication history for patient: {}", patientId);
        List<PatientMedicationHistoryResponse> history = patientMedicationHistoryService.getPatientMedicationHistory(patientId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{patientId}/history/{prescriptionId}")
    public ResponseEntity<PatientMedicationHistoryResponse> getPatientMedicationHistoryById(
            @PathVariable String patientId,
            @PathVariable String prescriptionId) {
        log.info("Fetching specific medication history for patient: {} with prescription: {}", patientId, prescriptionId);
        PatientMedicationHistoryResponse history = patientMedicationHistoryService.getPatientMedicationHistoryById(patientId, prescriptionId);
        if (history != null) {
            return ResponseEntity.ok(history);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{patientId}/history/date-range")
    public ResponseEntity<List<PatientMedicationHistoryResponse>> getPatientMedicationHistoryByDateRange(
            @PathVariable String patientId,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {
        log.info("Fetching medication history for patient: {} between {} and {}", patientId, startDate, endDate);
        List<PatientMedicationHistoryResponse> history = patientMedicationHistoryService
                .getPatientMedicationHistoryByDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{patientId}/history/recent")
    public ResponseEntity<List<PatientMedicationHistoryResponse>> getRecentPatientMedicationHistory(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "30") int days) {
        log.info("Fetching recent medication history for patient: {} (last {} days)", patientId, days);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        List<PatientMedicationHistoryResponse> history = patientMedicationHistoryService
                .getPatientMedicationHistoryByDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(history);
    }
}
