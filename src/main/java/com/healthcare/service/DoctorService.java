package com.healthcare.service;

import com.healthcare.model.*;
import com.healthcare.repository.*;
import com.healthcare.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private SecondOpinionRequestRepository secondOpinionRequestRepository;
    
    @Autowired
    private IdGenerator idGenerator;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Doctor registerDoctor(Doctor doctor, Long hospitalId) {
        // Find hospital
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(hospitalId);
        if (!hospitalOpt.isPresent()) {
            throw new IllegalArgumentException("Hospital not found with ID: " + hospitalId);
        }
        
        Hospital hospital = hospitalOpt.get();
        
        // Generate doctor code
        doctor.setDoctorCode(idGenerator.generateDoctorId());
        
        // Set hospital
        doctor.setHospital(hospital);
        
        // Create user for doctor with default password 123456
        String username = doctor.getName().toLowerCase().replaceAll("\\s+", ".") + "." + hospital.getName().toLowerCase().replaceAll("\\s+", "");
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("123456"))
                .role("DOCTOR")
                .firstLogin(true)
                .build();
        
        // Save user
        user = userRepository.save(user);
        
        // Set user to doctor
        doctor.setUser(user);
        
        // Save doctor
        return doctorRepository.save(doctor);
    }
    
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalId(hospitalId);
    }
    
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setFirstLogin(false);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    
    @Transactional
    public SecondOpinionRequest requestSecondOpinion(Long requestingDoctorId, Long patientId, Long consultingDoctorId, String conditionDescription, String currentTreatment) {
        Optional<Doctor> requestingDoctorOpt = doctorRepository.findById(requestingDoctorId);
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        Optional<Doctor> consultingDoctorOpt = doctorRepository.findById(consultingDoctorId);
        
        if (!requestingDoctorOpt.isPresent()) {
            throw new IllegalArgumentException("Requesting doctor not found with ID: " + requestingDoctorId);
        }
        
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }
        
        if (!consultingDoctorOpt.isPresent()) {
            throw new IllegalArgumentException("Consulting doctor not found with ID: " + consultingDoctorId);
        }
        
        SecondOpinionRequest request = SecondOpinionRequest.builder()
                .requestingDoctor(requestingDoctorOpt.get())
                .patient(patientOpt.get())
                .consultingDoctor(consultingDoctorOpt.get())
                .conditionDescription(conditionDescription)
                .currentTreatment(currentTreatment)
                .requestDate(LocalDateTime.now())
                .status("PENDING")
                .build();
        
        return secondOpinionRequestRepository.save(request);
    }
    
    public List<SecondOpinionRequest> getSecondOpinionRequestsForConsultingDoctor(Long doctorId) {
        return secondOpinionRequestRepository.findByConsultingDoctorId(doctorId);
    }
    
    public List<SecondOpinionRequest> getSecondOpinionRequestsByRequestingDoctor(Long doctorId) {
        return secondOpinionRequestRepository.findByRequestingDoctorId(doctorId);
    }
    
    @Transactional
    public SecondOpinionRequest provideSecondOpinion(Long requestId, String secondOpinion, String recommendedMedication, String recommendedTests) {
        Optional<SecondOpinionRequest> requestOpt = secondOpinionRequestRepository.findById(requestId);
        
        if (!requestOpt.isPresent()) {
            throw new IllegalArgumentException("Second opinion request not found with ID: " + requestId);
        }
        
        SecondOpinionRequest request = requestOpt.get();
        request.setSecondOpinion(secondOpinion);
        request.setRecommendedMedication(recommendedMedication);
        request.setRecommendedTests(recommendedTests);
        request.setResponseDate(LocalDateTime.now());
        request.setStatus("COMPLETED");
        
        return secondOpinionRequestRepository.save(request);
    }
}
