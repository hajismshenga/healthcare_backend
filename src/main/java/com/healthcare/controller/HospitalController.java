package com.healthcare.controller;

import com.healthcare.model.Hospital;
import com.healthcare.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/register")
    public ResponseEntity<?> registerHospital(@RequestBody Map<String, Object> request) {
        try {
            // Extract hospital data
            Hospital hospital = new Hospital();
            hospital.setName((String) request.get("name"));
            hospital.setOwnership((String) request.get("ownership"));
            hospital.setDistrict((String) request.get("district"));
            hospital.setLocation((String) request.get("location"));
            hospital.setContactInfo((String) request.get("contactInfo"));
            
            // Extract credentials
            String username = (String) request.get("username");
            String password = (String) request.get("password");
            
            // Register hospital
            Hospital registeredHospital = hospitalService.registerHospital(hospital, username, password);
            
            return ResponseEntity.ok(Map.of(
                "message", "Hospital registered successfully",
                "hospitalId", registeredHospital.getHospitalId(),
                "id", registeredHospital.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHospitalById(@PathVariable Long id) {
        Optional<Hospital> hospital = hospitalService.getHospitalById(id);
        if (hospital.isPresent()) {
            return ResponseEntity.ok(hospital.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Hospital> getAllHospitals() {
        return hospitalService.getAllHospitals();
    }
}
