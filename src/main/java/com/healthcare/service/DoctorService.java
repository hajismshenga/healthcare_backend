package com.healthcare.service;

import com.healthcare.dto.DoctorRegistrationRequest;
import com.healthcare.dto.LoginResponse;
import com.healthcare.exception.HospitalNotFoundException;
import com.healthcare.model.Doctor;
import com.healthcare.model.Hospital;
import com.healthcare.model.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.HospitalRepository;
import com.healthcare.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DoctorService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    public DoctorService(DoctorRepository doctorRepository, 
                        HospitalRepository hospitalRepository,
                        UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public Doctor registerDoctor(DoctorRegistrationRequest request) {
        log.debug("Starting doctor registration for ID: {}", request.getDoctorId());
        
        try {
            // Check if doctor ID already exists
            if (doctorRepository.existsByDoctorId(request.getDoctorId())) {
                log.warn("Doctor ID already exists: {}", request.getDoctorId());
                throw new IllegalArgumentException("Doctor ID already exists: " + request.getDoctorId());
            }

            // Verify hospital exists
            log.debug("Looking up hospital with ID: {}", request.getHospitalId());
            Hospital hospital = hospitalRepository.findByHospitalId(request.getHospitalId())
                .orElseThrow(() -> {
                    log.warn("Hospital not found with ID: {}", request.getHospitalId());
                    return new HospitalNotFoundException("Hospital not found with ID: " + request.getHospitalId());
                });

            log.debug("Creating new doctor entity for ID: {}", request.getDoctorId());
            
            // Create user first
            User user = new User();
            user.setUsername(request.getDoctorId().toLowerCase());
            user.setPassword(DEFAULT_PASSWORD);
            user.setRole("DOCTOR");
            
            // Save user first
            user = userRepository.save(user);
            log.debug("User saved with ID: {}", user.getId());
            
            // Create doctor with user relationship
            Doctor doctor = Doctor.builder()
                    .name(request.getName())
                    .profession(request.getProfession())
                    .doctorId(request.getDoctorId())
                    .password(DEFAULT_PASSWORD)
                    .hospital(hospital)
                    .user(user)
                    .build();
                    
            // Set the bidirectional relationship
            user.setDoctor(doctor);

            log.debug("Saving doctor to database: {}", doctor);
            Doctor savedDoctor = doctorRepository.save(doctor);
            log.info("Successfully registered doctor with ID: {}", savedDoctor.getDoctorId());
            
            return savedDoctor;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering doctor: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Could not register doctor. Please check the provided data.");
        } catch (Exception e) {
            log.error("Unexpected error while registering doctor: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while registering the doctor", e);
        }
    }

    public Optional<Doctor> findByDoctorId(String doctorId) {
        return doctorRepository.findByDoctorId(doctorId);
    }

    public List<Doctor> getDoctorsByHospital(String hospitalId) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findByHospitalId(hospitalId);
        if (hospitalOpt.isEmpty()) {
            throw new IllegalArgumentException("Hospital not found with ID: " + hospitalId);
        }
        return doctorRepository.findByHospital(hospitalOpt.get());
    }
    
    public LoginResponse authenticateDoctor(String doctorId, String password) {
        // Find the doctor by ID
        Optional<Doctor> doctorOpt = doctorRepository.findByDoctorId(doctorId);
        
        // Check if doctor exists
        if (doctorOpt.isEmpty()) {
            log.warn("Doctor not found with ID: {}", doctorId);
            return LoginResponse.error("Invalid doctor ID or password");
        }
        
        Doctor doctor = doctorOpt.get();
        User user = doctor.getUser();
        
        // Check if user exists and password matches
        if (user == null) {
            log.warn("No user account found for doctor ID: {}", doctorId);
            return LoginResponse.error("Invalid doctor ID or password");
        }
        
        if (!password.equals(user.getPassword())) {
            log.warn("Invalid password for doctor ID: {}", doctorId);
            return LoginResponse.error("Invalid doctor ID or password");
        }
        
        // In a real application, you would generate a proper JWT token here
        String token = "doctor-token-" + doctorId;
        log.info("Successfully authenticated doctor: {}", doctorId);
        
        return LoginResponse.fromDoctor(doctor, token);
    }
}
