package com.healthcare.controller;

import com.healthcare.dto.PatientRegistrationRequest;
import com.healthcare.dto.PatientLoginRequest;
import com.healthcare.dto.LoginResponse;
import com.healthcare.model.Patient;
import com.healthcare.service.PatientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
        log.info("PatientController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("PatientController is working!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegistrationRequest request, BindingResult bindingResult) {
        log.info("Received patient registration request: {}", request);
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for patient registration: {}", bindingResult.getAllErrors());
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
            log.debug("Attempting to register patient with ID: {}", request.getPatientId());
            Patient registeredPatient = patientService.registerPatient(request);
            log.info("Successfully registered patient with ID: {}", request.getPatientId());
            
            return ResponseEntity.status(201).body(Map.of(
                "status", "success",
                "message", "Patient registered successfully",
                "patientId", registeredPatient.getPatientId(),
                "defaultPassword", "123456"
            ));
        } catch (IllegalArgumentException e) {
            log.error("Error registering patient: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error registering patient: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Patient>> getPatientsByDoctor(@PathVariable String doctorId) {
        List<Patient> patients = patientService.getPatientsByDoctor(doctorId);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<Patient>> getPatientsByHospital(@PathVariable String hospitalId) {
        List<Patient> patients = patientService.getPatientsByHospital(hospitalId);
        return ResponseEntity.ok(patients);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(@Valid @RequestBody PatientLoginRequest request, BindingResult bindingResult) {
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

        LoginResponse response = patientService.authenticatePatient(request.getPatientId(), request.getPassword());
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
}
