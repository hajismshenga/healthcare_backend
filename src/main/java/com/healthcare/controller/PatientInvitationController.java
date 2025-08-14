package com.healthcare.controller;

import com.healthcare.dto.DoctorInvitationResponse;
import com.healthcare.dto.PatientInvitationRequest;
import com.healthcare.service.PatientInvitationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/patient-invitations")
public class PatientInvitationController {
    
    private final PatientInvitationService patientInvitationService;

    public PatientInvitationController(PatientInvitationService patientInvitationService) {
        this.patientInvitationService = patientInvitationService;
        log.info("PatientInvitationController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("PatientInvitationController is working!");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createInvitation(@Valid @RequestBody PatientInvitationRequest request, 
                                            BindingResult bindingResult) {
        log.info("Received invitation request from patient: {} to doctor: {}", 
                request.getPatientId(), request.getDoctorId());
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for invitation request: {}", bindingResult.getAllErrors());
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
            String invitationId = patientInvitationService.createInvitation(request);
            log.info("Successfully created invitation with ID: {}", invitationId);
            
            return ResponseEntity.status(201).body(Map.of(
                "status", "success",
                "message", "Invitation sent successfully",
                "invitationId", invitationId
            ));
        } catch (Exception e) {
            log.error("Error creating invitation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorInvitationResponse>> getDoctorInvitations(@PathVariable String doctorId) {
        log.info("Fetching invitations for doctor: {}", doctorId);
        List<DoctorInvitationResponse> invitations = patientInvitationService.getDoctorInvitations(doctorId);
        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/{invitationId}/respond")
    public ResponseEntity<?> respondToInvitation(@PathVariable String invitationId,
                                               @RequestParam String response,
                                               @RequestParam boolean accepted) {
        log.info("Doctor responding to invitation: {} with response: {}, accepted: {}", 
                invitationId, response, accepted);
        
        try {
            patientInvitationService.respondToInvitation(invitationId, response, accepted);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Response submitted successfully"
            ));
        } catch (Exception e) {
            log.error("Error responding to invitation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DoctorInvitationResponse>> getPatientInvitations(@PathVariable String patientId) {
        log.info("Fetching invitations for patient: {}", patientId);
        List<DoctorInvitationResponse> invitations = patientInvitationService.getPatientInvitations(patientId);
        return ResponseEntity.ok(invitations);
    }
}
