package com.healthcare.controller;

import com.healthcare.dto.DoctorRegistrationRequest;
import com.healthcare.model.Doctor;
import com.healthcare.service.DoctorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.healthcare.dto.DoctorLoginRequest;
import com.healthcare.dto.LoginResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
        log.info("DoctorController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("DoctorController is working!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRegistrationRequest request, BindingResult bindingResult) {
        log.info("Received doctor registration request: {}", request);
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for doctor registration: {}", bindingResult.getAllErrors());
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
            log.debug("Attempting to register doctor with ID: {}", request.getDoctorId());
            Doctor registeredDoctor = doctorService.registerDoctor(request);
            log.info("Successfully registered doctor with ID: {}", request.getDoctorId());
            
            return ResponseEntity.status(201).body(Map.of(
                "status", "success",
                "message", "Doctor registered successfully",
                "doctorId", registeredDoctor.getDoctorId(),
                "defaultPassword", "123456"
            ));
        } catch (IllegalArgumentException e) {
            log.error("Error registering doctor: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error registering doctor: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<Doctor>> getDoctorsByHospital(@PathVariable String hospitalId) {
        List<Doctor> doctors = doctorService.getDoctorsByHospital(hospitalId);
        return ResponseEntity.ok(doctors);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginDoctor(@Valid @RequestBody DoctorLoginRequest request, BindingResult bindingResult) {
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

        LoginResponse response = doctorService.authenticateDoctor(request.getDoctorId(), request.getPassword());
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
}
