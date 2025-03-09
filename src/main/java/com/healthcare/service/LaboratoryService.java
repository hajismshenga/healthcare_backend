package com.healthcare.service;

import com.healthcare.model.Laboratory;
import com.healthcare.repository.LaboratoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LaboratoryService {

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    public List<Laboratory> getAllLaboratories() {
        return laboratoryRepository.findAll();
    }

    public Optional<Laboratory> getLaboratoryById(Long id) {
        return laboratoryRepository.findById(id);
    }

    public List<Laboratory> getLaboratoriesByHospital(Long hospitalId) {
        return laboratoryRepository.findByHospitalId(hospitalId);
    }

    public Laboratory addLaboratory(Laboratory laboratory) {
        return laboratoryRepository.save(laboratory);
    }

    public void deleteLaboratory(Long id) {
        laboratoryRepository.deleteById(id);
    }
}
