package com.healthcare.service;

import com.healthcare.model.Hospital;
import com.healthcare.model.Laboratory;
import com.healthcare.model.User;
import com.healthcare.repository.HospitalRepository;
import com.healthcare.repository.LaboratoryRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LaboratoryService {

    @Autowired
    private LaboratoryRepository laboratoryRepository;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IdGenerator idGenerator;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Laboratory registerLaboratory(Laboratory laboratory, Long hospitalId) {
        // Find hospital
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(hospitalId);
        if (!hospitalOpt.isPresent()) {
            throw new IllegalArgumentException("Hospital not found with ID: " + hospitalId);
        }
        
        Hospital hospital = hospitalOpt.get();
        
        // Generate lab ID
        laboratory.setLabId(idGenerator.generateLabId());
        
        // Set hospital
        laboratory.setHospital(hospital);
        
        // Create user for laboratory
        String username = laboratory.getName().toLowerCase().replaceAll("\\s+", ".") + "." + hospital.getName().toLowerCase().replaceAll("\\s+", "");
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("123456"))
                .role("LAB")
                .firstLogin(true)
                .build();
        
        // Save user
        user = userRepository.save(user);
        
        // Set user to laboratory
        laboratory.setUser(user);
        
        // Save laboratory
        return laboratoryRepository.save(laboratory);
    }
    
    public Optional<Laboratory> getLaboratoryById(Long id) {
        return laboratoryRepository.findById(id);
    }

    public List<Laboratory> getAllLaboratories() {
        return laboratoryRepository.findAll();
    }

    public List<Laboratory> getLaboratoriesByHospital(Long hospitalId) {
        return laboratoryRepository.findByHospitalId(hospitalId);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        // Find user by username
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        
        User user = userOpt.get();
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false); // Reset first login flag
        userRepository.save(user);
    }
}
