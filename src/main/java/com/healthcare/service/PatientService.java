package com.healthcare.service;

import com.healthcare.dto.PatientRegistrationRequest;
import com.healthcare.dto.LoginResponse;
import com.healthcare.exception.DoctorNotFoundException;
import com.healthcare.model.Patient;
import com.healthcare.model.Doctor;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PatientService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PatientService(PatientRepository patientRepository, 
                         DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }
    
    @Transactional
    public Patient registerPatient(PatientRegistrationRequest request) {
        log.debug("Starting patient registration for ID: {}", request.getPatientId());
        
        try {
            // Check if patient ID already exists
            if (patientRepository.existsByPatientId(request.getPatientId())) {
                log.warn("Patient ID already exists: {}", request.getPatientId());
                throw new IllegalArgumentException("Patient ID already exists: " + request.getPatientId());
            }

            // Verify doctor exists
            log.debug("Looking up doctor with ID: {}", request.getDoctorId());
            Doctor doctor = doctorRepository.findByDoctorId(request.getDoctorId())
                .orElseThrow(() -> {
                    log.warn("Doctor not found with ID: {}", request.getDoctorId());
                    return new DoctorNotFoundException("Doctor not found with ID: " + request.getDoctorId());
                });

            log.debug("Creating new patient entity for ID: {}", request.getPatientId());
            Patient patient = Patient.builder()
                    .name(request.getName())
                    .patientId(request.getPatientId())
                    .password(DEFAULT_PASSWORD)
                    .doctor(doctor)
                    .hospital(doctor.getHospital())
                    .build();

            log.debug("Saving patient to database: {}", patient);
            Patient savedPatient = patientRepository.save(patient);
            log.info("Successfully registered patient with ID: {}", savedPatient.getPatientId());
            
            return savedPatient;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering patient: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Could not register patient. Please check the provided data.");
        } catch (Exception e) {
            log.error("Unexpected error while registering patient: {}", e.getMessage(), e);
            if (e.getCause() != null) {
                throw new RuntimeException("An unexpected error occurred while registering the patient: " + e.getCause().getMessage(), e);
            } else {
                throw new RuntimeException("An unexpected error occurred while registering the patient: " + e.getMessage(), e);
            }
        }
    }

    public Optional<Patient> findByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    public List<Patient> getPatientsByDoctor(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findByDoctorId(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }
        return patientRepository.findByDoctor(doctorOpt.get());
    }
    
    public List<Patient> getPatientsByHospital(String hospitalId) {
        return patientRepository.findByHospitalHospitalId(hospitalId);
    }
    
    public List<Patient> getAllPatients() {
        log.info("Fetching all patients");
        return patientRepository.findAll();
    }
    
    public LoginResponse authenticatePatient(String patientId, String password) {
        Optional<Patient> patientOpt = patientRepository.findByPatientId(patientId);
        
        if (patientOpt.isEmpty()) {
            return LoginResponse.error("Invalid patient ID or password");
        }
        
        Patient patient = patientOpt.get();
        if (!patient.getPassword().equals(password)) {
            return LoginResponse.error("Invalid patient ID or password");
        }
        
        // In a real application, you would generate a proper JWT token here
        String token = "patient-token-" + patientId;
        
        return LoginResponse.fromPatient(patient, token);
    }
}
