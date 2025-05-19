package com.healthcare.controller;

import com.healthcare.model.*;
import com.healthcare.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ClinicalRecordService clinicalRecordService;

    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@RequestBody Map<String, Object> request) {
        try {
            Doctor doctor = new Doctor();
            doctor.setName((String) request.get("name"));
            doctor.setSpecialty((String) request.get("specialty"));

            if (request.containsKey("additionalSpecialties")) {
                Object specialtiesObj = request.get("additionalSpecialties");
                if (specialtiesObj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<String> specialties = (List<String>) specialtiesObj;
                    doctor.setAdditionalSpecialties(specialties);
                }
            }

            doctor.setEmail((String) request.get("email"));
            doctor.setPhoneNumber((String) request.get("phoneNumber"));

            Long hospitalId = Long.valueOf(request.get("hospitalId").toString());

            Doctor registeredDoctor = doctorService.registerDoctor(doctor, hospitalId);

            return ResponseEntity.ok(Map.of(
                "message", "Doctor registered successfully",
                "doctorId", registeredDoctor.getDoctorCode(),
                "id", registeredDoctor.getId(),
                "username", registeredDoctor.getUser().getUsername(),
                "password", "123456"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        return doctor.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/hospital/{hospitalId}")
    public List<Doctor> getDoctorsByHospital(@PathVariable Long hospitalId) {
        return doctorService.getDoctorsByHospital(hospitalId);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (doctorService.changePassword(username, oldPassword, newPassword)) {
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/patients/register")
    public ResponseEntity<?> registerPatient(@RequestBody Map<String, Object> request) {
        try {
            Patient patient = new Patient();
            patient.setName((String) request.get("name"));
            patient.setAge(Integer.valueOf(request.get("age").toString()));
            patient.setGender((String) request.get("gender"));
            patient.setPhoneNumber((String) request.get("phoneNumber"));
            patient.setAddress((String) request.get("address"));

            Long doctorId = Long.valueOf(request.get("doctorId").toString());

            Patient registeredPatient = patientService.registerPatient(patient, doctorId);

            return ResponseEntity.ok(Map.of(
                "message", "Patient registered successfully",
                "patientId", registeredPatient.getPatientId(),
                "id", registeredPatient.getId(),
                "username", registeredPatient.getUser().getUsername(),
                "password", "123456"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getPatientsByDoctor(@RequestParam Long doctorId) {
        List<Patient> patients = patientService.getPatientsByDoctor(doctorId);
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/patients/{patientId}/clinical-summary")
    public ResponseEntity<?> updatePatientClinicalSummary(
            @PathVariable Long patientId,
            @RequestBody Map<String, String> request) {
        try {
            String clinicalSummary = request.get("clinicalSummary");
            Patient updatedPatient = patientService.updatePatientClinicalSummary(patientId, clinicalSummary);
            return ResponseEntity.ok(Map.of(
                "message", "Clinical summary updated successfully",
                "patientId", updatedPatient.getPatientId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/clinical-records")
    public ResponseEntity<?> createClinicalRecord(@RequestBody Map<String, Object> request) {
        try {
            Long patientId = Long.valueOf(request.get("patientId").toString());
            Long doctorId = Long.valueOf(request.get("doctorId").toString());

            ClinicalRecord clinicalRecord = new ClinicalRecord();
            clinicalRecord.setSymptoms((String) request.get("clinicalSummary"));
            clinicalRecord.setDiagnosis((String) request.get("diagnosis"));
            clinicalRecord.setTreatmentPlan((String) request.get("treatmentGiven"));
            clinicalRecord.setMedicationPrescribed((String) request.get("medicationPrescribed"));
            clinicalRecord.setNotes((String) request.get("doctorNotes"));
            clinicalRecord.setFollowUpInstructions((String) request.get("followUpInstructions"));
            clinicalRecord.setVisitDate(LocalDate.now());

            Optional<Doctor> doctorOpt = doctorService.getDoctorById(doctorId);
            if (!doctorOpt.isPresent()) {
                throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
            }
            clinicalRecord.setDoctor(doctorOpt.get());

            ClinicalRecord savedRecord = clinicalRecordService.createClinicalRecord(clinicalRecord, patientId, doctorId);

            return ResponseEntity.ok(Map.of(
                "message", "Clinical record created successfully",
                "recordId", savedRecord.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/clinical-records/doctor/{doctorId}")
    public ResponseEntity<List<ClinicalRecord>> getDoctorClinicalRecords(@PathVariable Long doctorId) {
        List<ClinicalRecord> records = clinicalRecordService.getDoctorClinicalRecords(doctorId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/clinical-records/patient/{patientId}")
    public ResponseEntity<List<ClinicalRecord>> getPatientClinicalRecords(@PathVariable Long patientId) {
        List<ClinicalRecord> records = clinicalRecordService.getPatientClinicalRecords(patientId);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/second-opinion/request")
    public ResponseEntity<?> requestSecondOpinion(@RequestBody Map<String, Object> request) {
        try {
            Long requestingDoctorId = Long.valueOf(request.get("requestingDoctorId").toString());
            Long patientId = Long.valueOf(request.get("patientId").toString());
            Long consultingDoctorId = Long.valueOf(request.get("consultingDoctorId").toString());
            String conditionDescription = (String) request.get("conditionDescription");
            String currentTreatment = (String) request.get("currentTreatment");

            SecondOpinionRequest secondOpinionRequest = doctorService.requestSecondOpinion(
                    requestingDoctorId, patientId, consultingDoctorId, conditionDescription, currentTreatment);

            return ResponseEntity.ok(Map.of(
                "message", "Second opinion request created successfully",
                "requestId", secondOpinionRequest.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/second-opinion/consulting/{doctorId}")
    public ResponseEntity<List<SecondOpinionRequest>> getSecondOpinionRequestsForConsulting(@PathVariable Long doctorId) {
        List<SecondOpinionRequest> requests = doctorService.getSecondOpinionRequestsForConsultingDoctor(doctorId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/second-opinion/requesting/{doctorId}")
    public ResponseEntity<List<SecondOpinionRequest>> getSecondOpinionRequestsByRequesting(@PathVariable Long doctorId) {
        List<SecondOpinionRequest> requests = doctorService.getSecondOpinionRequestsByRequestingDoctor(doctorId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/second-opinion/respond/{requestId}")
    public ResponseEntity<?> provideSecondOpinion(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> request) {
        try {
            String secondOpinion = request.get("secondOpinion");
            String recommendedMedication = request.get("recommendedMedication");
            String recommendedTests = request.get("recommendedTests");

            SecondOpinionRequest updatedRequest = doctorService.provideSecondOpinion(
                    requestId, secondOpinion, recommendedMedication, recommendedTests);

            return ResponseEntity.ok(Map.of(
                "message", "Second opinion provided successfully",
                "requestId", updatedRequest.getId(),
                "status", updatedRequest.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
