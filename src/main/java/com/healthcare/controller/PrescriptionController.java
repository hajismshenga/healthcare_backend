package com.healthcare.controller;

import com.healthcare.model.Prescription;
import com.healthcare.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // Get all prescriptions
    @GetMapping
    public List<Prescription> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    // Get a prescription by ID
    @GetMapping("/{id}")
    public Optional<Prescription> getPrescriptionById(@PathVariable Long id) {
        return prescriptionService.getPrescriptionById(id);
    }

    // Create a new prescription
    @PostMapping("/{doctorId}/{patientId}")
    public Prescription createPrescription(@PathVariable Long doctorId, 
                                           @PathVariable Long patientId, 
                                           @RequestBody Prescription prescription) {
        return prescriptionService.createPrescription(doctorId, patientId, prescription);
    }
}
