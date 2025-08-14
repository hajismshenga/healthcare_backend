package com.healthcare.controller;

import com.healthcare.dto.HospitalRegistrationRequest;
import com.healthcare.dto.LoginRequest;
import com.healthcare.dto.LoginResponse;
import com.healthcare.model.Hospital;
import com.healthcare.service.HospitalService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("HospitalController is working!");
    }
    
    @GetMapping
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        List<Hospital> hospitals = hospitalService.getAllHospitals();
        return ResponseEntity.ok(hospitals);
    }

    @GetMapping("/{hospitalId}")
    public ResponseEntity<Hospital> getHospitalById(@PathVariable String hospitalId) {
        Optional<Hospital> hospital = hospitalService.getHospitalByHospitalId(hospitalId);
        return hospital
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerHospital(@Valid @RequestBody HospitalRegistrationRequest request, BindingResult bindingResult) {
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
            Hospital hospital = Hospital.builder()
                .name(request.getName())
                .ownership(request.getOwnership()) // Service will handle uppercase conversion
                .district(request.getDistrict())
                .location(request.getLocation())
                .contactInfo(request.getContactInfo())
                .build();

            Hospital savedHospital = hospitalService.registerHospital(hospital, request.getUsername(), request.getPassword());
            
            return ResponseEntity.status(201).body(Map.of(
                "status", "success",
                "message", "Hospital registered successfully",
                "hospitalId", savedHospital.getHospitalId(),
                "username", request.getUsername()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred: " + ex.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginHospital(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
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

        LoginResponse response = hospitalService.authenticateUser(request.getUsername(), request.getPassword());
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }


}
