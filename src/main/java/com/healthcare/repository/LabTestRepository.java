package com.healthcare.repository;

import com.healthcare.model.LabTest;
import com.healthcare.model.Laboratory;
import com.healthcare.model.Patient;
import com.healthcare.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    
    Optional<LabTest> findByTestId(String testId);
    
    boolean existsByTestId(String testId);
    
    List<LabTest> findByLaboratory(Laboratory laboratory);
    
    List<LabTest> findByLaboratoryLaboratoryId(String laboratoryId);
    
    List<LabTest> findByPatient(Patient patient);
    
    List<LabTest> findByPatientPatientId(String patientId);
    
    List<LabTest> findByDoctor(Doctor doctor);
    
    List<LabTest> findByDoctorDoctorId(String doctorId);
    
    List<LabTest> findByStatus(LabTest.TestStatus status);
    
    @org.springframework.data.jpa.repository.Query("SELECT MAX(lt.testId) FROM LabTest lt WHERE lt.testId LIKE %:date%")
    String findMaxTestIdByDate(@org.springframework.data.repository.query.Param("date") String date);
}
