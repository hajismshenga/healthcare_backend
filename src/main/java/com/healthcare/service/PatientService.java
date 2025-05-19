package com.healthcare.service;

import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private UserRepository userRepository;
    

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Patient registerPatient(Patient patient, Long doctorId) {
        // Find doctor
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (!doctorOpt.isPresent()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }
        
        Doctor doctor = doctorOpt.get();
        
        // Generate patient ID (using a format like PAT/001)
        String patientId = "PAT/" + String.format("%03d", patientRepository.count() + 1);
        patient.setPatientId(patientId);
        
        // Set registration date
        patient.setRegistrationDate(LocalDate.now());
        
        // Set treating doctor
        patient.setTreatingDoctor(doctor);
        
        // Create user for patient with default password 123456
        String username = patient.getName().toLowerCase().replaceAll("\\s+", ".") + "." + patientId.toLowerCase();
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("123456"))
                .role("PATIENT")
                .firstLogin(true)
                .build();
        
        // Save user
        user = userRepository.save(user);
        
        // Set user to patient
        patient.setUser(user);
        
        // Save patient
        return patientRepository.save(patient);
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    public Optional<Patient> getPatientByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    public List<Patient> getPatientsByDoctor(Long doctorId) {
        return patientRepository.findByTreatingDoctorId(doctorId);
    }
    
    public Patient updatePatientClinicalSummary(Long patientId, String clinicalSummary) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.setClinicalSummary(clinicalSummary);
            return patientRepository.save(patient);
        }
        throw new IllegalArgumentException("Patient not found with ID: " + patientId);
    }
}
