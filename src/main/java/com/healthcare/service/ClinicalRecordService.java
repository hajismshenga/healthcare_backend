package com.healthcare.service;

import com.healthcare.model.ClinicalRecord;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.repository.ClinicalRecordRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
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
public class ClinicalRecordService {

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Transactional
    public ClinicalRecord createClinicalRecord(ClinicalRecord clinicalRecord, Long patientId, Long doctorId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }
        
        if (!doctorOpt.isPresent()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }
        
        clinicalRecord.setPatient(patientOpt.get());
        clinicalRecord.setDoctor(doctorOpt.get());
        clinicalRecord.setVisitDate(LocalDate.now());
        
        return clinicalRecordRepository.save(clinicalRecord);
    }
    
    public List<ClinicalRecord> getPatientClinicalRecords(Long patientId) {
        return clinicalRecordRepository.findByPatientId(patientId);
    }
    
    public Page<ClinicalRecord> getPatientClinicalRecordsPaginated(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("visitDate").descending());
        return clinicalRecordRepository.findByPatientId(patientId, pageable);
    }
    
    public List<ClinicalRecord> getRecentPatientClinicalRecords(Long patientId, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("visitDate").descending());
        Page<ClinicalRecord> recordPage = clinicalRecordRepository.findByPatientId(patientId, pageable);
        return recordPage.getContent();
    }
    
    public List<ClinicalRecord> getDoctorClinicalRecords(Long doctorId) {
        return clinicalRecordRepository.findByDoctorId(doctorId);
    }
    
    public Page<ClinicalRecord> getDoctorClinicalRecordsPaginated(Long doctorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("visitDate").descending());
        return clinicalRecordRepository.findByDoctorId(doctorId, pageable);
    }
    
    public List<ClinicalRecord> getRecentDoctorClinicalRecords(Long doctorId, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("visitDate").descending());
        Page<ClinicalRecord> recordPage = clinicalRecordRepository.findByDoctorId(doctorId, pageable);
        return recordPage.getContent();
    }
    
    public Optional<ClinicalRecord> getClinicalRecordById(Long id) {
        return clinicalRecordRepository.findById(id);
    }
    
    @Transactional
    public ClinicalRecord updateClinicalRecord(Long id, ClinicalRecord updatedRecord) {
        Optional<ClinicalRecord> existingRecordOpt = clinicalRecordRepository.findById(id);
        
        if (!existingRecordOpt.isPresent()) {
            throw new IllegalArgumentException("Clinical record not found with ID: " + id);
        }
        
        ClinicalRecord existingRecord = existingRecordOpt.get();
        
        // Update fields but preserve relationships and visit date
        existingRecord.setDiagnosis(updatedRecord.getDiagnosis());
        existingRecord.setSymptoms(updatedRecord.getSymptoms());
        existingRecord.setTreatmentPlan(updatedRecord.getTreatmentPlan());
        existingRecord.setNotes(updatedRecord.getNotes());
        
        return clinicalRecordRepository.save(existingRecord);
    }
    
    @Transactional
    public void deleteClinicalRecord(Long id) {
        if (!clinicalRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("Clinical record not found with ID: " + id);
        }
        
        clinicalRecordRepository.deleteById(id);
    }
    
    public List<ClinicalRecord> searchClinicalRecordsByDiagnosis(String diagnosisKeyword) {
        return clinicalRecordRepository.findByDiagnosisContainingIgnoreCase(diagnosisKeyword);
    }
    
    public List<ClinicalRecord> searchClinicalRecordsBySymptoms(String symptomsKeyword) {
        return clinicalRecordRepository.findBySymptomsContainingIgnoreCase(symptomsKeyword);
    }
}
