package com.healthcare.service;

import com.healthcare.model.LaboratoryTest;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.Laboratory;
import com.healthcare.repository.LaboratoryTestRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.LaboratoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LaboratoryTestService {

    @Autowired
    private LaboratoryTestRepository laboratoryTestRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    // Create a new laboratory test
    public LaboratoryTest createLaboratoryTest(Long doctorId, Long patientId, Long laboratoryId, LaboratoryTest laboratoryTest) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        Optional<Patient> patient = patientRepository.findById(patientId);
        Optional<Laboratory> laboratory = laboratoryRepository.findById(laboratoryId);

        if (doctor.isPresent() && patient.isPresent() && laboratory.isPresent()) {
            laboratoryTest.setDoctor(doctor.get());
            laboratoryTest.setPatient(patient.get());
            laboratoryTest.setLaboratory(laboratory.get());
            laboratoryTest.setTestDate(LocalDate.now());  // Set today's date
            return laboratoryTestRepository.save(laboratoryTest);
        } else {
            String errorMessage = "Doctor, Patient, or Laboratory not found";
            if (!doctor.isPresent()) errorMessage += " - Doctor not found";
            if (!patient.isPresent()) errorMessage += " - Patient not found";
            if (!laboratory.isPresent()) errorMessage += " - Laboratory not found";

            throw new RuntimeException(errorMessage);
        }
    }

    // Get all laboratory tests
    public List<LaboratoryTest> getAllLaboratoryTests() {
        return laboratoryTestRepository.findAll();
    }

    // Get laboratory test by ID
    public Optional<LaboratoryTest> getLaboratoryTestById(Long id) {
        return laboratoryTestRepository.findById(id);
    }
}
