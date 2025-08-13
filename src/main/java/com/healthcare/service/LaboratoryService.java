package com.healthcare.service;

import com.healthcare.dto.LaboratoryRegistrationRequest;
import com.healthcare.dto.LoginResponse;
import com.healthcare.exception.HospitalNotFoundException;
import com.healthcare.model.Laboratory;
import com.healthcare.model.Hospital;
import com.healthcare.repository.LaboratoryRepository;
import com.healthcare.repository.HospitalRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LaboratoryService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final LaboratoryRepository laboratoryRepository;
    private final HospitalRepository hospitalRepository;

    public LaboratoryService(LaboratoryRepository laboratoryRepository, 
                           HospitalRepository hospitalRepository) {
        this.laboratoryRepository = laboratoryRepository;
        this.hospitalRepository = hospitalRepository;
    }
    
    @Transactional
    public Laboratory registerLaboratory(LaboratoryRegistrationRequest request) {
        log.debug("Starting laboratory registration for ID: {}", request.getLaboratoryId());
        
        try {
            // Check if laboratory ID already exists
            if (laboratoryRepository.existsByLaboratoryId(request.getLaboratoryId())) {
                log.warn("Laboratory ID already exists: {}", request.getLaboratoryId());
                throw new IllegalArgumentException("Laboratory ID already exists: " + request.getLaboratoryId());
            }

            // Verify hospital exists
            log.debug("Looking up hospital with ID: {}", request.getHospitalId());
            Hospital hospital = hospitalRepository.findByHospitalId(request.getHospitalId())
                .orElseThrow(() -> {
                    log.warn("Hospital not found with ID: {}", request.getHospitalId());
                    return new HospitalNotFoundException("Hospital not found with ID: " + request.getHospitalId());
                });

            log.debug("Creating new laboratory entity for ID: {}", request.getLaboratoryId());
            Laboratory laboratory = Laboratory.builder()
                    .name(request.getName())
                    .laboratoryId(request.getLaboratoryId())
                    .password(DEFAULT_PASSWORD)
                    .hospital(hospital)
                    .build();

            log.debug("Saving laboratory to database: {}", laboratory);
            Laboratory savedLaboratory = laboratoryRepository.save(laboratory);
            log.info("Successfully registered laboratory with ID: {}", savedLaboratory.getLaboratoryId());
            
            return savedLaboratory;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering laboratory: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Could not register laboratory. Please check the provided data.");
        } catch (Exception e) {
            log.error("Unexpected error while registering laboratory: {}", e.getMessage(), e);
            if (e.getCause() != null) {
                throw new RuntimeException("An unexpected error occurred while registering the laboratory: " + e.getCause().getMessage(), e);
            } else {
                throw new RuntimeException("An unexpected error occurred while registering the laboratory: " + e.getMessage(), e);
            }
        }
    }

    public Optional<Laboratory> findByLaboratoryId(String laboratoryId) {
        return laboratoryRepository.findByLaboratoryId(laboratoryId);
    }

    public List<Laboratory> getLaboratoriesByHospital(String hospitalId) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findByHospitalId(hospitalId);
        if (hospitalOpt.isEmpty()) {
            throw new IllegalArgumentException("Hospital not found with ID: " + hospitalId);
        }
        return laboratoryRepository.findByHospital(hospitalOpt.get());
    }
    
    public List<Laboratory> getAllLaboratories() {
        log.info("Fetching all laboratories");
        return laboratoryRepository.findAll();
    }
    
    public LoginResponse authenticateLaboratory(String laboratoryId, String password) {
        Optional<Laboratory> laboratoryOpt = laboratoryRepository.findByLaboratoryId(laboratoryId);
        
        if (laboratoryOpt.isEmpty()) {
            return LoginResponse.error("Invalid laboratory ID or password");
        }
        
        Laboratory laboratory = laboratoryOpt.get();
        if (!laboratory.getPassword().equals(password)) {
            return LoginResponse.error("Invalid laboratory ID or password");
        }
        
        // In a real application, you would generate a proper JWT token here
        String token = "laboratory-token-" + laboratoryId;
        
        return LoginResponse.fromLaboratory(laboratory, token);
    }
}
