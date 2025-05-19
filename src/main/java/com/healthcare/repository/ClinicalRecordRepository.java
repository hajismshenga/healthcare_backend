package com.healthcare.repository;

import com.healthcare.model.ClinicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {
    List<ClinicalRecord> findByPatientId(Long patientId);
    org.springframework.data.domain.Page<ClinicalRecord> findByPatientId(Long patientId, org.springframework.data.domain.Pageable pageable);
    
    List<ClinicalRecord> findByDoctorId(Long doctorId);
    Page<ClinicalRecord> findByDoctorId(Long doctorId, Pageable pageable);
    
    List<ClinicalRecord> findByPatientIdAndVisitDateBetween(Long patientId, LocalDate startDate, LocalDate endDate);
    List<ClinicalRecord> findByDoctorIdAndVisitDateBetween(Long doctorId, LocalDate startDate, LocalDate endDate);
    
    List<ClinicalRecord> findByDiagnosisContainingIgnoreCase(String diagnosisKeyword);
    List<ClinicalRecord> findBySymptomsContainingIgnoreCase(String symptomsKeyword);
    
    @Query("SELECT cr FROM ClinicalRecord cr WHERE cr.patient.id = :patientId AND cr.doctor.id = :doctorId")
    List<ClinicalRecord> findByPatientIdAndDoctorId(Long patientId, Long doctorId);
}
