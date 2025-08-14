package com.healthcare.controller;

import com.healthcare.dto.PrescriptionRequest;
import com.healthcare.dto.PrescriptionResponse;
import com.healthcare.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    
    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
        log.info("PrescriptionController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("PrescriptionController is working!");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPrescription(@Valid @RequestBody PrescriptionRequest request, 
                                               BindingResult bindingResult,
                                               @RequestParam String doctorId) {
        log.info("Received prescription request from doctor: {} for patient: {}", doctorId, request.getPatientId());
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for prescription request: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Validation failed",
                "errors", bindingResult.getFieldErrors().stream()
                    .collect(java.util.stream.Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage()
                    ))
            ));
        }

        try {
            log.debug("Attempting to create prescription for doctor: {}", doctorId);
            PrescriptionResponse createdPrescription = prescriptionService.createPrescription(request, doctorId);
            log.info("Successfully created prescription with ID: {}", createdPrescription.getPrescriptionId());
            
            return ResponseEntity.status(201).body(Map.of(
                "status", "success",
                "message", "Prescription created successfully",
                "prescription", createdPrescription
            ));
        } catch (IllegalArgumentException e) {
            log.error("Error creating prescription: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error creating prescription: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByPatient(@PathVariable String patientId) {
        log.info("Fetching prescriptions for patient: {}", patientId);
        List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByDoctor(@PathVariable String doctorId) {
        log.info("Fetching prescriptions by doctor: {}", doctorId);
        List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByHospital(@PathVariable String hospitalId) {
        log.info("Fetching prescriptions for hospital: {}", hospitalId);
        List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByHospital(hospitalId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{prescriptionId}")
    public ResponseEntity<?> getPrescriptionById(@PathVariable String prescriptionId) {
        log.info("Fetching prescription by ID: {}", prescriptionId);
        return prescriptionService.getPrescriptionById(prescriptionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> getAllPrescriptions() {
        log.info("Fetching all prescriptions");
        List<PrescriptionResponse> prescriptions = prescriptionService.getAllPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/patient/{patientId}/date-range")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByDateRange(
            @PathVariable String patientId,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {
        log.info("Fetching prescriptions for patient: {} between {} and {}", patientId, startDate, endDate);
        List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(prescriptions);
    }
}
