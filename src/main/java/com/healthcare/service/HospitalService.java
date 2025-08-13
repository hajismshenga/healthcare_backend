package com.healthcare.service;

import com.healthcare.dto.LoginResponse;
import com.healthcare.model.Hospital;
import com.healthcare.model.User;
import com.healthcare.repository.HospitalRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.util.IdGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IdGenerator idGenerator;
    

    
    @Transactional
    public Hospital registerHospital(Hospital hospital, String username, String password) {
        log.info("Starting hospital registration for: {}", hospital.getName());
        log.info("Username: {}", username);
        
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        username = username.trim();
        
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            log.warn("Username already exists: {}", username);
            throw new IllegalArgumentException("Username already exists");
        }

        try {
            // Create and save user first
            log.info("Creating new user with username: {}", username);
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // Password should be encoded in a real application
            user.setRole("HOSPITAL");
            user = userRepository.save(user);
            log.info("User created successfully with ID: {}", user.getId());
            
            // Generate hospital ID and save hospital
            String hospitalId = idGenerator.generateHospitalId();
            log.info("Generated hospital ID: {}", hospitalId);
            
            hospital.setHospitalId(hospitalId);
            hospital.setUser(user);
            
            Hospital savedHospital = hospitalRepository.save(hospital);
            log.info("Hospital registered successfully with ID: {}", savedHospital.getId());
            
            return savedHospital;
        } catch (Exception e) {
            log.error("Error during hospital registration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register hospital: " + e.getMessage(), e);
        }
    }
    
    public Optional<Hospital> getHospitalById(Long id) {
        return hospitalRepository.findById(id);
    }

    public List<Hospital> getAllHospitals() {
        log.info("Fetching all hospitals");
        return hospitalRepository.findAll();
    }
    
    public Optional<Hospital> getHospitalByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> hospitalRepository.findByUser(user));
    }
    
    public Optional<Hospital> getHospitalByHospitalId(String hospitalId) {
        return hospitalRepository.findByHospitalId(hospitalId);
    }
    
    public LoginResponse authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return LoginResponse.error("Invalid username or password");
        }
        
        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return LoginResponse.error("Invalid username or password");
        }
        
        Optional<Hospital> hospitalOpt = hospitalRepository.findByUser(user);
        if (hospitalOpt.isEmpty()) {
            return LoginResponse.error("No hospital associated with this user");
        }
        
        String token = "dummy-token-" + user.getUsername();
        return LoginResponse.fromHospital(hospitalOpt.get(), token);
    }
}
