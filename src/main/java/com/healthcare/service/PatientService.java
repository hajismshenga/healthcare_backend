package com.healthcare.service;

import com.healthcare.model.Patient;
import com.healthcare.model.Doctor;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // Fetch all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Fetch a patient by ID
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Register a new patient via a doctor
    public Patient addPatient(Long doctorId, Patient patient) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if (doctor.isPresent()) {
            patient.setDoctor(doctor.get());
            return patientRepository.save(patient);
        } else {
            throw new RuntimeException("Doctor not found!");
        }
    }

    // Delete a patient
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
