package com.healthcare.controller;

import com.healthcare.model.DoctorEvaluation;
import com.healthcare.service.DoctorEvaluationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor-evaluations")
public class DoctorEvaluationController {

    @Autowired
    private DoctorEvaluationService doctorEvaluationService;

    // Create a new doctor evaluation
    @PostMapping("/{doctorId}/{patientId}")
    public DoctorEvaluation createDoctorEvaluation(@PathVariable Long doctorId,
                                                  @PathVariable Long patientId,
                                                  @RequestBody DoctorEvaluation doctorEvaluation) {
        return doctorEvaluationService.createDoctorEvaluation(doctorId, patientId, doctorEvaluation);
    }

    // Optionally, you can add a GET endpoint to retrieve evaluations
    @GetMapping("/{doctorId}")
    public List<DoctorEvaluation> getDoctorEvaluations(@PathVariable Long doctorId) {
        // Retrieve all evaluations for a specific doctor
        return doctorEvaluationService.getDoctorEvaluations(doctorId);
    }
}
