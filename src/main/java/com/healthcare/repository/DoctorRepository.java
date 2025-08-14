package com.healthcare.repository;

import com.healthcare.model.Doctor;
import com.healthcare.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByDoctorId(String doctorId);
    Optional<Doctor> findByDoctorId(String doctorId);

    // Retrieve doctors by hospital
    List<Doctor> findByHospital(Hospital hospital);
    
    // Retrieve doctors by profession
    List<Doctor> findByProfession(String profession);
}
