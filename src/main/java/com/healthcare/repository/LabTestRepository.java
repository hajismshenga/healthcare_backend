package com.healthcare.repository;

import com.healthcare.model.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    List<LabTest> findByPatientId(Long patientId);
    List<LabTest> findByRequestingDoctorId(Long doctorId);
    List<LabTest> findByLaboratoryId(Long laboratoryId);
    List<LabTest> findByStatus(String status);
    List<LabTest> findByLaboratoryIdAndStatus(Long laboratoryId, String status);
    List<LabTest> findByClinicalRecordId(Long clinicalRecordId);
    List<LabTest> findByPatientIdAndStatus(Long patientId, String status);
    List<LabTest> findByRequestingDoctorIdAndStatus(Long doctorId, String status);
    // Removed findByDoctorId(Long doctorId);
}

