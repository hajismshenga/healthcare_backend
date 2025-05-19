package com.healthcare.controller;

import com.healthcare.model.SecondOpinion;
import com.healthcare.service.SecondOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/second-opinions")
public class SecondOpinionController {

    @Autowired
    private SecondOpinionService secondOpinionService;

    @PostMapping("/request")
    public ResponseEntity<?> requestSecondOpinion(@RequestBody Map<String, Object> request) {
        try {
            Long patientId = Long.parseLong((String) request.get("patientId"));
            Long requestingDoctorId = Long.parseLong((String) request.get("requestingDoctorId"));
            String caseSummary = (String) request.get("caseSummary");
            String currentTreatment = (String) request.get("currentTreatment");
            String additionalInformation = (String) request.get("additionalInformation");

            SecondOpinion opinion = secondOpinionService.requestSecondOpinion(
                patientId, requestingDoctorId, caseSummary, currentTreatment, additionalInformation
            );

            return ResponseEntity.ok(Map.of(
                "message", "Second opinion request submitted successfully",
                "opinionId", opinion.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOpinions(@RequestParam Long doctorId) {
        List<SecondOpinion> opinions = secondOpinionService.getPendingOpinions(doctorId);
        return ResponseEntity.ok(opinions);
    }

    @PostMapping("/review")
    public ResponseEntity<?> reviewSecondOpinion(@RequestBody Map<String, Object> request) {
        try {
            Long opinionId = Long.parseLong((String) request.get("opinionId"));
            Long reviewingDoctorId = Long.parseLong((String) request.get("reviewingDoctorId"));
            String notes = (String) request.get("notes");
            String medications = (String) request.get("medications");
            String tests = (String) request.get("tests");
            String recommendations = (String) request.get("recommendations");

            SecondOpinion opinion = secondOpinionService.reviewSecondOpinion(
                opinionId, reviewingDoctorId, notes, medications, tests, recommendations
            );

            return ResponseEntity.ok(Map.of(
                "message", "Second opinion reviewed successfully",
                "opinionId", opinion.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}
