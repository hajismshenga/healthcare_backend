package com.healthcare.service;

import com.healthcare.model.Hospital;
import com.healthcare.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public Optional<Hospital> getHospitalById(Long id) {
        return hospitalRepository.findById(id);
    }

    public Hospital addHospital(Hospital hospital) {
        return hospitalRepository.save(hospital);
    }

    public void deleteHospital(Long id) {
        hospitalRepository.deleteById(id);
    }
}
