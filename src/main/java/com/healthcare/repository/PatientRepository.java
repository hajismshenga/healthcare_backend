package com.healthcare.repository;

import com.healthcare.model.Patient;
import com.healthcare.model.Doctor;
import com.healthcare.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByPatientId(String patientId);
    
    boolean existsByPatientId(String patientId);
    
    List<Patient> findByDoctor(Doctor doctor);
    
    List<Patient> findByDoctorDoctorId(String doctorId);
    
    List<Patient> findByHospital(Hospital hospital);
    
    List<Patient> findByHospitalHospitalId(String hospitalId);
}
