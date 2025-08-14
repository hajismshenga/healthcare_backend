package com.healthcare.controller;

import com.healthcare.dto.LabTestRequest;
import com.healthcare.dto.LabResultSubmissionRequest;
import com.healthcare.model.LabTest;
import com.healthcare.service.LabTestService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/lab-tests")
public class LabTestController {
    
    private final LabTestService labTestService;

    public LabTestController(LabTestService labTestService) {
        this.labTestService = labTestService;
        log.info("LabTestController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("LabTestController is working!");
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestLabTest(@Valid @RequestBody LabTestRequest request, 
                                           BindingResult bindingResult,
                                           @RequestParam String doctorId) {
        log.info("Received lab test request from doctor: {} for patient: {}", doctorId, request.getPatientId());
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for lab test request: {}", bindingResult.getAllErrors());
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
            log.debug("Attempting to create lab test for doctor: {}", doctorId);
            LabTest createdTest = labTestService.createLabTest(request, doctorId);
            log.info("Successfully created lab test with ID: {}", createdTest.getTestId());
            
            return ResponseEntity.status(201).body(Map.of(
                "status", "success",
                "message", "Lab test requested successfully",
                "testId", createdTest.getTestId(),
                "laboratoryId", createdTest.getLaboratory().getLaboratoryId(),
                "patientId", createdTest.getPatient().getPatientId(),
                "patientName", createdTest.getPatient().getName(),
                "testRequirement", createdTest.getTestRequirement(),
                "testStatus", createdTest.getStatus().toString(),
                "requestedDate", createdTest.getRequestedDate()
            ));
        } catch (IllegalArgumentException e) {
            log.error("Error creating lab test: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error creating lab test: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<LabTest>> getTestsByDoctor(@PathVariable String doctorId) {
        List<LabTest> tests = labTestService.getTestsByDoctor(doctorId);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/laboratory/{laboratoryId}")
    public ResponseEntity<List<LabTest>> getTestsByLaboratory(@PathVariable String laboratoryId) {
        List<LabTest> tests = labTestService.getTestsByLaboratory(laboratoryId);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabTest>> getTestsByPatient(@PathVariable String patientId) {
        List<LabTest> tests = labTestService.getTestsByPatient(patientId);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LabTest>> getTestsByStatus(@PathVariable String status) {
        try {
            LabTest.TestStatus testStatus = LabTest.TestStatus.valueOf(status.toUpperCase());
            List<LabTest> tests = labTestService.getTestsByStatus(testStatus);
            return ResponseEntity.ok(tests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<LabTest>> getAllTests() {
        List<LabTest> tests = labTestService.getAllTests();
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/{testId}")
    public ResponseEntity<?> getTestById(@PathVariable String testId) {
        return labTestService.findByTestId(testId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{testId}/status")
    public ResponseEntity<?> updateTestStatus(@PathVariable String testId,
                                             @RequestParam String status,
                                             @RequestParam(required = false) String notes) {
        try {
            LabTest.TestStatus testStatus = LabTest.TestStatus.valueOf(status.toUpperCase());
            LabTest updatedTest = labTestService.updateTestStatus(testId, testStatus, notes);
            return ResponseEntity.ok(updatedTest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Invalid status or test not found: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{testId}/result")
    public ResponseEntity<?> addTestResult(@PathVariable String testId,
                                          @RequestBody Map<String, String> request) {
        String testResult = request.get("testResult");
        if (testResult == null || testResult.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Test result is required"
            ));
        }

        try {
            LabTest updatedTest = labTestService.addTestResult(testId, testResult);
            return ResponseEntity.ok(updatedTest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/results")
    public ResponseEntity<?> submitLabResult(@Valid @RequestBody LabResultSubmissionRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
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
            LabTest updated = labTestService.addTestResultByIdentifiers(request);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Lab result submitted and linked to latest pending test",
                "testId", updated.getTestId(),
                "patientId", updated.getPatient().getPatientId(),
                "patientName", updated.getPatient().getName(),
                "doctorId", updated.getDoctor().getDoctorId(),
                "result", updated.getTestResult(),
                "completedDate", updated.getCompletedDate()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}
