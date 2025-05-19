package com.healthcare.repository;

import com.healthcare.model.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    Optional<LabResult> findByLabTestId(Long labTestId);
    
    @Query("SELECT lr FROM LabResult lr JOIN lr.labTest lt WHERE lt.patient.id = :patientId")
    List<LabResult> findByPatientId(Long patientId);
    
    @Query("SELECT lr FROM LabResult lr JOIN lr.labTest lt WHERE lt.requestingDoctor.id = :doctorId")
    List<LabResult> findByDoctorId(Long doctorId);
    
    @Query("SELECT lr FROM LabResult lr JOIN lr.labTest lt WHERE lt.laboratory.id = :laboratoryId")
    List<LabResult> findByLaboratoryId(Long laboratoryId);
}
