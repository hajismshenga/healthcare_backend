package com.healthcare.repository;

import com.healthcare.model.Laboratory;
import com.healthcare.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    
    Optional<Laboratory> findByLaboratoryId(String laboratoryId);
    
    boolean existsByLaboratoryId(String laboratoryId);
    
    List<Laboratory> findByHospital(Hospital hospital);
    
    List<Laboratory> findByHospitalHospitalId(String hospitalId);
}
