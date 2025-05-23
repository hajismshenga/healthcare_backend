package com.healthcare.repository;

import com.healthcare.model.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    Optional<Laboratory> findByLabId(String labId);
    List<Laboratory> findByHospitalId(Long hospitalId);
}
