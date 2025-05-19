package com.healthcare.controller;

import com.healthcare.model.Prescription;
import com.healthcare.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping("/issue")
    public ResponseEntity<?> issuePrescription(@RequestBody Map<String, Object> request) {
        try {
            Long patientId = Long.valueOf(request.get("patientId").toString());
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            String medications = (String) request.get("medications");
            String instructions = (String) request.get("instructions");
            Integer durationDays = Integer.valueOf(request.get("durationDays").toString());
            String diagnosis = (String) request.get("diagnosis");
            
            Long secondOpinionRequestId = null;
            if (request.containsKey("secondOpinionRequestId") && request.get("secondOpinionRequestId") != null) {
                secondOpinionRequestId = Long.valueOf(request.get("secondOpinionRequestId").toString());
            }
            
            Prescription prescription = prescriptionService.createPrescription(
                patientId, doctorId, medications, instructions, durationDays, secondOpinionRequestId
            );
            
            prescription.setDiagnosis(diagnosis);
            prescriptionService.savePrescription(prescription);
            
            return ResponseEntity.ok(Map.of(
                "message", "Prescription issued successfully",
                "prescriptionId", prescription.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/from-clinical-record")
    public ResponseEntity<?> issuePrescriptionFromClinicalRecord(@RequestBody Map<String, Object> request) {
        try {
            Long clinicalRecordId = Long.valueOf(request.get("clinicalRecordId").toString());
            String medications = (String) request.get("medications");
            String instructions = (String) request.get("instructions");
            Integer durationDays = Integer.valueOf(request.get("durationDays").toString());
            
            Prescription prescription = prescriptionService.createPrescriptionFromClinicalRecord(
                clinicalRecordId, medications, instructions, durationDays
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Prescription issued successfully from clinical record",
                "prescriptionId", prescription.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Prescription> prescriptionOpt = prescriptionService.getPrescriptionById(id);
        if (prescriptionOpt.isPresent()) {
            return ResponseEntity.ok(prescriptionOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Prescription>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatient(patientId));
    }
    
    @GetMapping("/patient/{patientId}/paginated")
    public ResponseEntity<Page<Prescription>> getByPatientPaginated(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientPaginated(patientId, page, size));
    }
    
    @GetMapping("/patient/{patientId}/recent")
    public ResponseEntity<List<Prescription>> getRecentByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(prescriptionService.getRecentPrescriptionsByPatient(patientId, count));
    }
    
    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<List<Prescription>> getActiveByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getActivePrescriptionsByPatient(patientId));
    }
    
    @GetMapping("/patient/{patientId}/date-range")
    public ResponseEntity<List<Prescription>> getByPatientAndDateRange(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientIdAndDateRange(patientId, startDate, endDate));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctor(doctorId));
    }
    
    @GetMapping("/doctor/{doctorId}/paginated")
    public ResponseEntity<Page<Prescription>> getByDoctorPaginated(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctorPaginated(doctorId, page, size));
    }
    
    @GetMapping("/doctor/{doctorId}/recent")
    public ResponseEntity<List<Prescription>> getRecentByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(prescriptionService.getRecentPrescriptionsByDoctor(doctorId, count));
    }
    
    @GetMapping("/doctor/{doctorId}/date-range")
    public ResponseEntity<List<Prescription>> getByDoctorAndDateRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctorIdAndDateRange(doctorId, startDate, endDate));
    }
    
    @GetMapping("/second-opinion/{secondOpinionRequestId}")
    public ResponseEntity<List<Prescription>> getBySecondOpinionRequest(@PathVariable Long secondOpinionRequestId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsBySecondOpinionRequest(secondOpinionRequestId));
    }
    
    @GetMapping("/clinical-record/{clinicalRecordId}")
    public ResponseEntity<List<Prescription>> getByClinicalRecord(@PathVariable Long clinicalRecordId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByClinicalRecord(clinicalRecordId));
    }
    
    @GetMapping("/controlled-substances")
    public ResponseEntity<List<Prescription>> getAllControlledSubstances() {
        return ResponseEntity.ok(prescriptionService.getAllControlledSubstancePrescriptions());
    }
    
    @GetMapping("/controlled-substances/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getControlledSubstancesByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(prescriptionService.getControlledSubstancePrescriptionsByDoctor(doctorId));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updatePrescriptionStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusData) {
        try {
            String status = statusData.get("status");
            Prescription prescription = prescriptionService.updatePrescriptionStatus(id, status);
            return ResponseEntity.ok(Map.of(
                "message", "Prescription status updated successfully",
                "prescriptionId", prescription.getId(),
                "status", prescription.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/refill")
    public ResponseEntity<?> processPrescriptionRefill(@PathVariable Long id) {
        try {
            Prescription prescription = prescriptionService.processRefill(id);
            return ResponseEntity.ok(Map.of(
                "message", "Prescription refilled successfully",
                "prescriptionId", prescription.getId(),
                "refillsRemaining", prescription.getRefillsRemaining()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrescription(
            @PathVariable Long id,
            @RequestBody Prescription updatedPrescription) {
        try {
            Prescription prescription = prescriptionService.updatePrescription(id, updatedPrescription);
            return ResponseEntity.ok(prescription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrescription(@PathVariable Long id) {
        try {
            prescriptionService.deletePrescription(id);
            return ResponseEntity.ok(Map.of("message", "Prescription deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
