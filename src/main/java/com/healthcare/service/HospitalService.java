package com.healthcare.service;

import com.healthcare.model.Hospital;
import com.healthcare.model.User;
import com.healthcare.repository.HospitalRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IdGenerator idGenerator;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Hospital registerHospital(Hospital hospital, String username, String password) {
        // Create user for hospital
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role("HOSPITAL")
                .build();
        
        // Save user
        user = userRepository.save(user);
        
        // Generate hospital ID
        hospital.setHospitalId(idGenerator.generateHospitalId());
        
        // Set user to hospital
        hospital.setUser(user);
        
        // Save hospital
        return hospitalRepository.save(hospital);
    }
    
    public Optional<Hospital> getHospitalById(Long id) {
        return hospitalRepository.findById(id);
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }
    
    public Optional<Hospital> getHospitalByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return hospitalRepository.findAll().stream()
                    .filter(h -> h.getUser() != null && h.getUser().getId().equals(user.getId()))
                    .findFirst();
        }
        return Optional.empty();
    }
}
