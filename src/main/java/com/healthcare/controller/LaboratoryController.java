package com.healthcare.controller;

import com.healthcare.model.LabResult;
import com.healthcare.model.LabTest;
import com.healthcare.model.Laboratory;
import com.healthcare.service.LabTestService;
import com.healthcare.service.LaboratoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/laboratories")
public class LaboratoryController {

    @Autowired
    private LaboratoryService laboratoryService;
    
    @Autowired
    private LabTestService labTestService;

    @PostMapping("/register")
    public ResponseEntity<?> registerLaboratory(@RequestBody Map<String, Object> request) {
        try {
            // Extract laboratory data
            Laboratory laboratory = new Laboratory();
            laboratory.setName((String) request.get("name"));
            laboratory.setSpecialization((String) request.get("specialization"));
            
            // Extract hospital ID
            Long hospitalId = Long.valueOf(request.get("hospitalId").toString());
            
            // Register laboratory
            Laboratory registeredLab = laboratoryService.registerLaboratory(laboratory, hospitalId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Laboratory registered successfully",
                "labId", registeredLab.getLabId(),
                "id", registeredLab.getId(),
                "username", registeredLab.getUser().getUsername(),
                "password", "123456"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLaboratoryById(@PathVariable Long id) {
        Optional<Laboratory> laboratory = laboratoryService.getLaboratoryById(id);
        if (laboratory.isPresent()) {
            return ResponseEntity.ok(laboratory.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Laboratory>> getAllLaboratories() {
        return ResponseEntity.ok(laboratoryService.getAllLaboratories());
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<Laboratory>> getLaboratoriesByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(laboratoryService.getLaboratoriesByHospital(hospitalId));
    }
    
    @GetMapping("/{labId}/pending-tests")
    public ResponseEntity<List<LabTest>> getPendingTests(@PathVariable Long labId) {
        return ResponseEntity.ok(labTestService.getPendingTestsByLaboratory(labId));
    }
    
    @GetMapping("/{labId}/tests")
    public ResponseEntity<List<LabTest>> getAllTests(@PathVariable Long labId) {
        return ResponseEntity.ok(labTestService.getTestsByLaboratory(labId));
    }
    
    @GetMapping("/{labId}/results")
    public ResponseEntity<List<LabResult>> getAllResults(@PathVariable Long labId) {
        return ResponseEntity.ok(labTestService.getResultsByLaboratory(labId));
    }
    
    @PostMapping("/{labId}/process-test/{testId}")
    public ResponseEntity<?> processTest(@PathVariable Long labId, @PathVariable Long testId, @RequestBody Map<String, Object> request) {
        try {
            String resultDetails = (String) request.get("resultDetails");
            String interpretation = (String) request.get("interpretation");
            String normalRange = (String) request.get("normalRange");
            String notes = (String) request.get("notes");
            String recommendations = (String) request.get("recommendations");
            
            // Verify that the test belongs to this laboratory
            Optional<LabTest> testOpt = labTestService.getTestById(testId);
            if (!testOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Test not found"));
            }
            
            LabTest test = testOpt.get();
            if (!test.getLaboratory().getId().equals(labId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "This test does not belong to this laboratory"));
            }
            
            LabResult result = labTestService.addTestResult(testId, resultDetails, interpretation, normalRange, notes, recommendations, recommendations);
            
            return ResponseEntity.ok(Map.of(
                "message", "Test processed successfully",
                "resultId", result.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            laboratoryService.changePassword(username, oldPassword, newPassword);
            
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
