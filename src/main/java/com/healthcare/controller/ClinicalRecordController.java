package com.healthcare.controller;

import com.healthcare.model.ClinicalRecord;
import com.healthcare.service.ClinicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clinical-records")
public class ClinicalRecordController {

    @Autowired
    private ClinicalRecordService clinicalRecordService;

    @PostMapping
    public ResponseEntity<?> createClinicalRecord(@RequestBody Map<String, Object> request) {
        try {
            Long patientId = Long.valueOf(request.get("patientId").toString());
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            
            ClinicalRecord clinicalRecord = new ClinicalRecord();
            clinicalRecord.setDiagnosis((String) request.get("diagnosis"));
            clinicalRecord.setSymptoms((String) request.get("symptoms"));
            clinicalRecord.setTreatmentPlan((String) request.get("treatmentPlan"));
            clinicalRecord.setNotes((String) request.get("notes"));
            
            ClinicalRecord savedRecord = clinicalRecordService.createClinicalRecord(clinicalRecord, patientId, doctorId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Clinical record created successfully",
                "recordId", savedRecord.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getClinicalRecordById(@PathVariable Long id) {
        Optional<ClinicalRecord> recordOpt = clinicalRecordService.getClinicalRecordById(id);
        if (recordOpt.isPresent()) {
            return ResponseEntity.ok(recordOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ClinicalRecord>> getPatientClinicalRecords(@PathVariable Long patientId) {
        return ResponseEntity.ok(clinicalRecordService.getPatientClinicalRecords(patientId));
    }
    
    @GetMapping("/patient/{patientId}/paginated")
    public ResponseEntity<Page<ClinicalRecord>> getPatientClinicalRecordsPaginated(
            @PathVariable Long patientId, 
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(clinicalRecordService.getPatientClinicalRecordsPaginated(patientId, page, size));
    }
    
    @GetMapping("/patient/{patientId}/recent")
    public ResponseEntity<List<ClinicalRecord>> getRecentPatientClinicalRecords(
            @PathVariable Long patientId, 
            @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(clinicalRecordService.getRecentPatientClinicalRecords(patientId, count));
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ClinicalRecord>> getDoctorClinicalRecords(@PathVariable Long doctorId) {
        return ResponseEntity.ok(clinicalRecordService.getDoctorClinicalRecords(doctorId));
    }
    
    @GetMapping("/doctor/{doctorId}/paginated")
    public ResponseEntity<Page<ClinicalRecord>> getDoctorClinicalRecordsPaginated(
            @PathVariable Long doctorId, 
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(clinicalRecordService.getDoctorClinicalRecordsPaginated(doctorId, page, size));
    }
    
    @GetMapping("/doctor/{doctorId}/recent")
    public ResponseEntity<List<ClinicalRecord>> getRecentDoctorClinicalRecords(
            @PathVariable Long doctorId, 
            @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(clinicalRecordService.getRecentDoctorClinicalRecords(doctorId, count));
    }
    
    @GetMapping("/search/diagnosis")
    public ResponseEntity<List<ClinicalRecord>> searchByDiagnosis(@RequestParam String keyword) {
        return ResponseEntity.ok(clinicalRecordService.searchClinicalRecordsByDiagnosis(keyword));
    }
    
    @GetMapping("/search/symptoms")
    public ResponseEntity<List<ClinicalRecord>> searchBySymptoms(@RequestParam String keyword) {
        return ResponseEntity.ok(clinicalRecordService.searchClinicalRecordsBySymptoms(keyword));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClinicalRecord(@PathVariable Long id, @RequestBody ClinicalRecord updatedRecord) {
        try {
            ClinicalRecord record = clinicalRecordService.updateClinicalRecord(id, updatedRecord);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClinicalRecord(@PathVariable Long id) {
        try {
            clinicalRecordService.deleteClinicalRecord(id);
            return ResponseEntity.ok(Map.of("message", "Clinical record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
