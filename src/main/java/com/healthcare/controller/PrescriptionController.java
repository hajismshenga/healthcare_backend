package com.healthcare.controller;

import com.healthcare.model.Prescription;
import com.healthcare.service.PrescriptionService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // Create a new prescription
    @PostMapping("/{doctorId}/{patientId}")
    public Prescription createPrescription(@PathVariable Long doctorId,
                                           @PathVariable Long patientId,
                                           @RequestBody Prescription prescription) {
        return prescriptionService.createPrescription(doctorId, patientId, prescription);
    }

    // Optionally, you can add a GET endpoint to retrieve all prescriptions for a patient
    @GetMapping("/{patientId}")
    public List<Prescription> getPatientPrescriptions(@PathVariable Long patientId) {
        // Retrieve all prescriptions for a specific patient
        return prescriptionService.getPatientPrescriptions(patientId);
    }
}
