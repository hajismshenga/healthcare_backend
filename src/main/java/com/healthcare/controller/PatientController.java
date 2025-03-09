package com.healthcare.controller;

import com.healthcare.model.Patient;
import com.healthcare.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Get all patients
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    // Get a patient by ID
    @GetMapping("/{id}")
    public Optional<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    // Register a patient via a doctor
    @PostMapping("/{doctorId}")
    public Patient addPatient(@PathVariable Long doctorId, @RequestBody Patient patient) {
        return patientService.addPatient(doctorId, patient);
    }

    // Delete a patient
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }
}
