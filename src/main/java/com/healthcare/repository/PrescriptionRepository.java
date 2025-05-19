package com.healthcare.repository;

import com.healthcare.model.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);
    Page<Prescription> findByPatientId(Long patientId, Pageable pageable);
    
    List<Prescription> findByDoctorId(Long doctorId);
    Page<Prescription> findByDoctorId(Long doctorId, Pageable pageable);
    
    List<Prescription> findBySecondOpinionRequestId(Long secondOpinionRequestId);
    
    List<Prescription> findByClinicalRecordId(Long clinicalRecordId);
    
    List<Prescription> findByPatientIdAndStatus(Long patientId, String status);
    List<Prescription> findByDoctorIdAndStatus(Long doctorId, String status);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId AND p.dateIssued BETWEEN :startDate AND :endDate")
    List<Prescription> findByPatientIdAndDateRange(Long patientId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor.id = :doctorId AND p.dateIssued BETWEEN :startDate AND :endDate")
    List<Prescription> findByDoctorIdAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId AND p.validUntil >= :currentDate AND p.status = 'ACTIVE'")
    List<Prescription> findActiveByPatientId(Long patientId, LocalDate currentDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.isControlledSubstance = true")
    List<Prescription> findAllControlledSubstancePrescriptions();
    
    @Query("SELECT p FROM Prescription p WHERE p.isControlledSubstance = true AND p.doctor.id = :doctorId")
    List<Prescription> findControlledSubstancePrescriptionsByDoctor(Long doctorId);
}
