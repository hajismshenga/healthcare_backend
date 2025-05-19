package com.healthcare.service;

import com.healthcare.model.ClinicalRecord;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.Prescription;
import com.healthcare.model.SecondOpinionRequest;
import com.healthcare.repository.ClinicalRecordRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.repository.SecondOpinionRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private SecondOpinionRequestRepository secondOpinionRequestRepository;
    
    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    @Transactional
    public Prescription createPrescription(Long patientId, Long doctorId, String medications, 
                                          String instructions, Integer durationDays, Long secondOpinionRequestId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }
        
        if (!doctorOpt.isPresent()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }
        
        Prescription prescription = new Prescription();
        prescription.setPatient(patientOpt.get());
        prescription.setDoctor(doctorOpt.get());
        prescription.setMedications(medications);
        prescription.setInstructions(instructions);
        prescription.setDurationDays(durationDays);
        prescription.setDateIssued(LocalDate.now());
        prescription.setValidUntil(LocalDate.now().plusDays(durationDays));
        prescription.setStatus("ACTIVE");
        
        if (secondOpinionRequestId != null) {
            Optional<SecondOpinionRequest> requestOpt = secondOpinionRequestRepository.findById(secondOpinionRequestId);
            if (requestOpt.isPresent()) {
                prescription.setSecondOpinionRequest(requestOpt.get());
                prescription.setIsSecondOpinion(true);
            }
        }
        
        return prescriptionRepository.save(prescription);
    }
    
    @Transactional
    public Prescription createPrescriptionFromClinicalRecord(Long clinicalRecordId, String medications, 
                                                           String instructions, Integer durationDays) {
        Optional<ClinicalRecord> recordOpt = clinicalRecordRepository.findById(clinicalRecordId);
        
        if (!recordOpt.isPresent()) {
            throw new IllegalArgumentException("Clinical record not found with ID: " + clinicalRecordId);
        }
        
        ClinicalRecord record = recordOpt.get();
        
        Prescription prescription = new Prescription();
        prescription.setPatient(record.getPatient());
        prescription.setDoctor(record.getDoctor());
        prescription.setClinicalRecord(record);
        prescription.setDiagnosis(record.getDiagnosis());
        prescription.setMedications(medications);
        prescription.setInstructions(instructions);
        prescription.setDurationDays(durationDays);
        prescription.setDateIssued(LocalDate.now());
        prescription.setValidUntil(LocalDate.now().plusDays(durationDays));
        prescription.setStatus("ACTIVE");
        
        return prescriptionRepository.save(prescription);
    }

    public Prescription savePrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    public Optional<Prescription> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }

    public List<Prescription> getPrescriptionsByPatient(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }
    
    public Page<Prescription> getPrescriptionsByPatientPaginated(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateIssued").descending());
        return prescriptionRepository.findByPatientId(patientId, pageable);
    }
    
    public List<Prescription> getRecentPrescriptionsByPatient(Long patientId, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("dateIssued").descending());
        Page<Prescription> prescriptionPage = prescriptionRepository.findByPatientId(patientId, pageable);
        return prescriptionPage.getContent();
    }
    
    public List<Prescription> getActivePrescriptionsByPatient(Long patientId) {
        return prescriptionRepository.findByPatientIdAndStatus(patientId, "ACTIVE");
    }

    public List<Prescription> getPrescriptionsByDoctor(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }
    
    public Page<Prescription> getPrescriptionsByDoctorPaginated(Long doctorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateIssued").descending());
        return prescriptionRepository.findByDoctorId(doctorId, pageable);
    }
    
    public List<Prescription> getRecentPrescriptionsByDoctor(Long doctorId, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("dateIssued").descending());
        Page<Prescription> prescriptionPage = prescriptionRepository.findByDoctorId(doctorId, pageable);
        return prescriptionPage.getContent();
    }
    
    public List<Prescription> getPrescriptionsBySecondOpinionRequest(Long secondOpinionRequestId) {
        return prescriptionRepository.findBySecondOpinionRequestId(secondOpinionRequestId);
    }
    
    public List<Prescription> getPrescriptionsByClinicalRecord(Long clinicalRecordId) {
        return prescriptionRepository.findByClinicalRecordId(clinicalRecordId);
    }
    
    public List<Prescription> getPrescriptionsByPatientIdAndDateRange(Long patientId, LocalDate startDate, LocalDate endDate) {
        return prescriptionRepository.findByPatientIdAndDateRange(patientId, startDate, endDate);
    }
    
    public List<Prescription> getPrescriptionsByDoctorIdAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return prescriptionRepository.findByDoctorIdAndDateRange(doctorId, startDate, endDate);
    }
    
    public List<Prescription> getAllControlledSubstancePrescriptions() {
        return prescriptionRepository.findAllControlledSubstancePrescriptions();
    }
    
    public List<Prescription> getControlledSubstancePrescriptionsByDoctor(Long doctorId) {
        return prescriptionRepository.findControlledSubstancePrescriptionsByDoctor(doctorId);
    }
    
    @Transactional
    public Prescription updatePrescriptionStatus(Long id, String status) {
        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(id);
        
        if (!prescriptionOpt.isPresent()) {
            throw new IllegalArgumentException("Prescription not found with ID: " + id);
        }
        
        Prescription prescription = prescriptionOpt.get();
        prescription.setStatus(status);
        
        if (status.equals("FILLED") && prescription.getFilledDate() == null) {
            prescription.setFilledDate(LocalDate.now());
        }
        
        return prescriptionRepository.save(prescription);
    }
    
    @Transactional
    public Prescription updatePrescription(Long id, Prescription updatedPrescription) {
        Optional<Prescription> existingPrescriptionOpt = prescriptionRepository.findById(id);
        
        if (!existingPrescriptionOpt.isPresent()) {
            throw new IllegalArgumentException("Prescription not found with ID: " + id);
        }
        
        Prescription existingPrescription = existingPrescriptionOpt.get();
        
        // Update fields but preserve relationships
        existingPrescription.setDiagnosis(updatedPrescription.getDiagnosis());
        existingPrescription.setMedications(updatedPrescription.getMedications());
        existingPrescription.setInstructions(updatedPrescription.getInstructions());
        existingPrescription.setDurationDays(updatedPrescription.getDurationDays());
        existingPrescription.setValidUntil(updatedPrescription.getValidUntil());
        existingPrescription.setStatus(updatedPrescription.getStatus());
        existingPrescription.setRefillsAllowed(updatedPrescription.getRefillsAllowed());
        existingPrescription.setRefillsRemaining(updatedPrescription.getRefillsRemaining());
        
        return prescriptionRepository.save(existingPrescription);
    }
    
    @Transactional
    public Prescription processRefill(Long id) {
        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(id);
        
        if (!prescriptionOpt.isPresent()) {
            throw new IllegalArgumentException("Prescription not found with ID: " + id);
        }
        
        Prescription prescription = prescriptionOpt.get();
        
        if (prescription.getRefillsRemaining() <= 0) {
            throw new IllegalArgumentException("No refills remaining for prescription with ID: " + id);
        }
        
        prescription.setRefillsRemaining(prescription.getRefillsRemaining() - 1);
        prescription.setFilledDate(LocalDate.now());
        prescription.setStatus("FILLED");
        
        return prescriptionRepository.save(prescription);
    }
    
    @Transactional
    public void deletePrescription(Long id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new IllegalArgumentException("Prescription not found with ID: " + id);
        }
        prescriptionRepository.deleteById(id);
    }
}
