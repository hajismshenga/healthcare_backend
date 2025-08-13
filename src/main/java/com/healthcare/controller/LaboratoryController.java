package com.healthcare.controller;

import com.healthcare.dto.LaboratoryRegistrationRequest;
import com.healthcare.dto.LaboratoryLoginRequest;
import com.healthcare.dto.LoginResponse;
import com.healthcare.model.Laboratory;
import com.healthcare.service.LaboratoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/laboratories")
public class LaboratoryController {
    
    private final LaboratoryService laboratoryService;

    public LaboratoryController(LaboratoryService laboratoryService) {
        this.laboratoryService = laboratoryService;
        log.info("LaboratoryController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("LaboratoryController is working!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerLaboratory(@Valid @RequestBody LaboratoryRegistrationRequest request, BindingResult bindingResult) {
        log.info("Received laboratory registration request: {}", request);
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for laboratory registration: {}", bindingResult.getAllErrors());
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
            log.debug("Attempting to register laboratory with ID: {}", request.getLaboratoryId());
            Laboratory registeredLaboratory = laboratoryService.registerLaboratory(request);
            log.info("Successfully registered laboratory with ID: {}", request.getLaboratoryId());
            
            return ResponseEntity.status(201).body(Map.of(
                "status", "success",
                "message", "Laboratory registered successfully",
                "laboratoryId", registeredLaboratory.getLaboratoryId(),
                "defaultPassword", "123456"
            ));
        } catch (IllegalArgumentException e) {
            log.error("Error registering laboratory: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error registering laboratory: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<Laboratory>> getLaboratoriesByHospital(@PathVariable String hospitalId) {
        List<Laboratory> laboratories = laboratoryService.getLaboratoriesByHospital(hospitalId);
        return ResponseEntity.ok(laboratories);
    }

    @GetMapping
    public ResponseEntity<List<Laboratory>> getAllLaboratories() {
        List<Laboratory> laboratories = laboratoryService.getAllLaboratories();
        return ResponseEntity.ok(laboratories);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginLaboratory(@Valid @RequestBody LaboratoryLoginRequest request, BindingResult bindingResult) {
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

        LoginResponse response = laboratoryService.authenticateLaboratory(request.getLaboratoryId(), request.getPassword());
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
}
