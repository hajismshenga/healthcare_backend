package com.healthcare.service;

import com.healthcare.model.Prescription;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    // Create a new prescription
    public Prescription createPrescription(Long doctorId, Long patientId, Prescription prescription) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        Optional<Patient> patient = patientRepository.findById(patientId);

        if (doctor.isPresent() && patient.isPresent()) {
            prescription.setDoctor(doctor.get());
            prescription.setPatient(patient.get());
            prescription.setPrescribedDate(LocalDate.now()); // Set today's date
            return prescriptionRepository.save(prescription);
        } else {
            throw new RuntimeException("Doctor or Patient not found");
        }
    }

    // Get all prescriptions
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    // Get prescription by ID
    public Optional<Prescription> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }
}
