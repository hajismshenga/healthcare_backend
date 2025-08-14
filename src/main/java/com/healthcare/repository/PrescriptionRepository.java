package com.healthcare.repository;

import com.healthcare.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    
    Optional<Prescription> findByPrescriptionId(String prescriptionId);
    
    boolean existsByPrescriptionId(String prescriptionId);
    
    List<Prescription> findByPatientPatientId(String patientId);
    
    List<Prescription> findByDoctorDoctorId(String doctorId);
    
    List<Prescription> findByHospitalHospitalId(String hospitalId);
    
    List<Prescription> findByPatientPatientIdAndPrescriptionDateBetween(String patientId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT MAX(p.prescriptionId) FROM Prescription p WHERE p.prescriptionId LIKE %?1%")
    String findMaxPrescriptionId(String prefix);
}
