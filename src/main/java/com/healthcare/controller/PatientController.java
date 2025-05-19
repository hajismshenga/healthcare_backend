package com.healthcare.controller;

import com.healthcare.model.*;
import com.healthcare.repository.UserRepository;
import com.healthcare.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;
    
    @Autowired
    private ClinicalRecordService clinicalRecordService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private SecondOpinionRequestService secondOpinionRequestService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private LabTestService labTestService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        Optional<Patient> patientOpt = patientService.getPatientById(id);
        if (patientOpt.isPresent()) {
            return ResponseEntity.ok(patientOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/patientId/{patientId}")
    public ResponseEntity<?> getPatientByPatientId(@PathVariable String patientId) {
        Optional<Patient> patientOpt = patientService.getPatientByPatientId(patientId);
        if (patientOpt.isPresent()) {
            return ResponseEntity.ok(patientOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setFirstLogin(false);
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid old password"));
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/clinical-records/{patientId}")
    public ResponseEntity<List<ClinicalRecord>> getPatientClinicalRecords(@PathVariable Long patientId) {
        List<ClinicalRecord> records = clinicalRecordService.getPatientClinicalRecords(patientId);
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/prescriptions/{patientId}")
    public ResponseEntity<List<Prescription>> getPatientPrescriptions(@PathVariable Long patientId) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/prescriptions/{patientId}/active")
    public ResponseEntity<List<Prescription>> getPatientActivePrescriptions(@PathVariable Long patientId) {
        List<Prescription> prescriptions = prescriptionService.getActivePrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/lab-tests/{patientId}")
    public ResponseEntity<List<LabTest>> getPatientLabTests(@PathVariable Long patientId) {
        List<LabTest> tests = labTestService.getTestsByPatient(patientId);
        return ResponseEntity.ok(tests);
    }
    
    @GetMapping("/lab-results/{patientId}")
    public ResponseEntity<List<LabResult>> getPatientLabResults(@PathVariable Long patientId) {
        List<LabResult> results = labTestService.getResultsByPatient(patientId);
        return ResponseEntity.ok(results);
    }
    
    @PostMapping("/request-second-opinion")
    public ResponseEntity<?> requestSecondOpinion(@RequestBody Map<String, Object> request) {
        try {
            Long patientId = Long.valueOf(request.get("patientId").toString());
            Long requestingDoctorId = Long.valueOf(request.get("requestingDoctorId").toString());
            Long consultingDoctorId = Long.valueOf(request.get("consultingDoctorId").toString());
            String conditionDescription = (String) request.get("conditionDescription");
            String currentTreatment = (String) request.get("currentTreatment");
            boolean isUrgent = Boolean.valueOf(request.get("isUrgent").toString());
            
            Long clinicalRecordId = null;
            if (request.containsKey("clinicalRecordId") && request.get("clinicalRecordId") != null) {
                clinicalRecordId = Long.valueOf(request.get("clinicalRecordId").toString());
            }
            
            SecondOpinionRequest secondOpinionRequest = secondOpinionRequestService.createSecondOpinionRequest(
                patientId, requestingDoctorId, consultingDoctorId, conditionDescription, currentTreatment, isUrgent);
            
            if (clinicalRecordId != null) {
                secondOpinionRequestService.linkToClinicalRecord(secondOpinionRequest.getId(), clinicalRecordId);
            }
            
            return ResponseEntity.ok(Map.of(
                "message", "Second opinion request created successfully",
                "requestId", secondOpinionRequest.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/second-opinion-requests/{patientId}")
    public ResponseEntity<List<SecondOpinionRequest>> getPatientSecondOpinionRequests(@PathVariable Long patientId) {
        List<SecondOpinionRequest> requests = secondOpinionRequestService.getRequestsByPatient(patientId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/dashboard/{patientId}")
    public ResponseEntity<?> getPatientDashboard(@PathVariable Long patientId) {
        try {
            Optional<Patient> patientOpt = patientService.getPatientById(patientId);
            if (!patientOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Patient patient = patientOpt.get();
            List<ClinicalRecord> recentRecords = clinicalRecordService.getRecentPatientClinicalRecords(patientId, 5);
            List<Prescription> activePrescriptions = prescriptionService.getActivePrescriptionsByPatient(patientId);
            List<LabTest> pendingTests = labTestService.getTestsByPatientAndStatus(patientId, "PENDING");
            List<LabResult> recentResults = labTestService.getRecentResultsByPatient(patientId, 5);
            List<SecondOpinionRequest> pendingOpinionRequests = secondOpinionRequestService.getPendingSecondOpinionRequestsByPatient(patientId);
            
            return ResponseEntity.ok(Map.of(
                "patient", patient,
                "recentRecords", recentRecords,
                "activePrescriptions", activePrescriptions,
                "pendingTests", pendingTests,
                "recentResults", recentResults,
                "pendingOpinionRequests", pendingOpinionRequests
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}
