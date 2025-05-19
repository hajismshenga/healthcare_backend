package com.healthcare.controller;

import com.healthcare.model.LabResult;
import com.healthcare.model.LabTest;
import com.healthcare.service.LabTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/lab-tests")
public class LabTestController {

    @Autowired
    private LabTestService labTestService;

    @PostMapping("/request")
    public ResponseEntity<?> requestLabTest(@RequestBody Map<String, Object> request) {
        try {
            Long patientId = Long.valueOf(request.get("patientId").toString());
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            Long laboratoryId = Long.valueOf(request.get("laboratoryId").toString());
            String testType = (String) request.get("testType");
            String testDescription = (String) request.get("testDescription");
            
            Long clinicalRecordId = null;
            if (request.containsKey("clinicalRecordId") && request.get("clinicalRecordId") != null) {
                clinicalRecordId = Long.valueOf(request.get("clinicalRecordId").toString());
            }
            
            LabTest test = labTestService.requestTest(patientId, doctorId, laboratoryId, testType, testDescription, clinicalRecordId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Lab test requested successfully",
                "testId", test.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<LabTest>> getAllTests() {
        return ResponseEntity.ok(labTestService.getAllTests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTest(@PathVariable Long id) {
        Optional<LabTest> testOpt = labTestService.getTestById(id);
        if (testOpt.isPresent()) {
            return ResponseEntity.ok(testOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabTest>> getTestsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(labTestService.getTestsByPatient(patientId));
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<LabTest>> getTestsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(labTestService.getTestsByDoctor(doctorId));
    }
    
    @GetMapping("/laboratory/{laboratoryId}")
    public ResponseEntity<List<LabTest>> getTestsByLaboratory(@PathVariable Long laboratoryId) {
        return ResponseEntity.ok(labTestService.getTestsByLaboratory(laboratoryId));
    }
    
    @GetMapping("/laboratory/{laboratoryId}/pending")
    public ResponseEntity<List<LabTest>> getPendingTestsByLaboratory(@PathVariable Long laboratoryId) {
        return ResponseEntity.ok(labTestService.getPendingTestsByLaboratory(laboratoryId));
    }
    
    @PostMapping("/results")
    public ResponseEntity<?> addTestResult(@RequestBody Map<String, Object> request) {
        try {
            Long testId = Long.valueOf(request.get("testId").toString());
            String resultDetails = (String) request.get("resultDetails");
            String interpretation = (String) request.get("interpretation");
            String normalRange = (String) request.get("normalRange");
            String notes = (String) request.get("notes");
            String recommendations = (String) request.get("recommendations");
            String resultsSummaryForPatient = (String) request.get("resultsSummaryForPatient");
            
            LabResult result = labTestService.addTestResult(testId, resultDetails, interpretation, normalRange, notes, recommendations, resultsSummaryForPatient);
            
            return ResponseEntity.ok(Map.of(
                "message", "Lab result added successfully",
                "resultId", result.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/results/test/{testId}")
    public ResponseEntity<?> getResultByTestId(@PathVariable Long testId) {
        Optional<LabResult> resultOpt = labTestService.getResultByTestId(testId);
        if (resultOpt.isPresent()) {
            return ResponseEntity.ok(resultOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/results/patient/{patientId}")
    public ResponseEntity<List<LabResult>> getResultsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(labTestService.getResultsByPatient(patientId));
    }
    
    @GetMapping("/results/doctor/{doctorId}")
    public ResponseEntity<List<LabResult>> getResultsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(labTestService.getResultsByDoctor(doctorId));
    }
    
    @GetMapping("/results/laboratory/{laboratoryId}")
    public ResponseEntity<List<LabResult>> getResultsByLaboratory(@PathVariable Long laboratoryId) {
        return ResponseEntity.ok(labTestService.getResultsByLaboratory(laboratoryId));
    }
    
    @GetMapping("/clinical-record/{clinicalRecordId}")
    public ResponseEntity<List<LabTest>> getTestsByClinicalRecord(@PathVariable Long clinicalRecordId) {
        return ResponseEntity.ok(labTestService.getTestsByClinicalRecord(clinicalRecordId));
    }
    
    @PostMapping("/request-from-clinical-record")
    public ResponseEntity<?> requestTestFromClinicalRecord(@RequestBody Map<String, Object> request) {
        try {
            Long clinicalRecordId = Long.valueOf(request.get("clinicalRecordId").toString());
            Long laboratoryId = Long.valueOf(request.get("laboratoryId").toString());
            String testType = (String) request.get("testType");
            String testDescription = (String) request.get("testDescription");
            
            LabTest test = labTestService.requestTestFromClinicalRecord(clinicalRecordId, laboratoryId, testType, testDescription);
            
            return ResponseEntity.ok(Map.of(
                "message", "Lab test requested successfully from clinical record",
                "testId", test.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}
