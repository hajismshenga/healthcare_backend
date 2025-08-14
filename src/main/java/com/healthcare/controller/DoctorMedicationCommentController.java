package com.healthcare.controller;

import com.healthcare.dto.DoctorMedicationComment;
import com.healthcare.service.DoctorMedicationCommentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/doctor-medication-comments")
public class DoctorMedicationCommentController {
    
    private final DoctorMedicationCommentService doctorMedicationCommentService;

    public DoctorMedicationCommentController(DoctorMedicationCommentService doctorMedicationCommentService) {
        this.doctorMedicationCommentService = doctorMedicationCommentService;
        log.info("DoctorMedicationCommentController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("DoctorMedicationCommentController is working!");
    }

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@Valid @RequestBody DoctorMedicationComment commentRequest, 
                                      BindingResult bindingResult) {
        log.info("Received medication comment request from doctor: {} for patient: {} on prescription: {}", 
                commentRequest.getDoctorId(), commentRequest.getPatientId(), commentRequest.getPrescriptionId());
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for comment request: {}", bindingResult.getAllErrors());
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
            String commentId = doctorMedicationCommentService.addComment(commentRequest);
            log.info("Successfully added medication comment with ID: {}", commentId);
            
            return ResponseEntity.status(201).body(Map.of(
                "status", "success",
                "message", "Comment added successfully",
                "commentId", commentId
            ));
        } catch (Exception e) {
            log.error("Error adding comment: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DoctorMedicationComment>> getPatientComments(@PathVariable String patientId) {
        log.info("Fetching medication comments for patient: {}", patientId);
        List<DoctorMedicationComment> comments = doctorMedicationCommentService.getPatientComments(patientId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorMedicationComment>> getDoctorComments(@PathVariable String doctorId) {
        log.info("Fetching medication comments by doctor: {}", doctorId);
        List<DoctorMedicationComment> comments = doctorMedicationCommentService.getDoctorComments(doctorId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/prescription/{prescriptionId}")
    public ResponseEntity<List<DoctorMedicationComment>> getPrescriptionComments(@PathVariable String prescriptionId) {
        log.info("Fetching medication comments for prescription: {}", prescriptionId);
        List<DoctorMedicationComment> comments = doctorMedicationCommentService.getPrescriptionComments(prescriptionId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable String commentId,
                                         @Valid @RequestBody DoctorMedicationComment updatedComment,
                                         BindingResult bindingResult) {
        log.info("Updating medication comment: {}", commentId);
        
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for comment update: {}", bindingResult.getAllErrors());
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
            doctorMedicationCommentService.updateComment(commentId, updatedComment);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Comment updated successfully"
            ));
        } catch (Exception e) {
            log.error("Error updating comment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
        log.info("Deleting medication comment: {}", commentId);
        
        try {
            doctorMedicationCommentService.deleteComment(commentId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Comment deleted successfully"
            ));
        } catch (Exception e) {
            log.error("Error deleting comment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}
