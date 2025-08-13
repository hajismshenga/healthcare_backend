package com.healthcare.repository;

import com.healthcare.model.Hospital;
import com.healthcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByHospitalId(String hospitalId);
    boolean existsByHospitalId(String hospitalId);
    Optional<Hospital> findByUser(User user);
    
    @Query("SELECT MAX(h.hospitalId) FROM Hospital h")
    String findMaxHospitalId();
}
