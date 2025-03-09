package com.healthcare.service;

import com.healthcare.model.DoctorEvaluation;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.repository.DoctorEvaluationRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorEvaluationService {

    @Autowired
    private DoctorEvaluationRepository doctorEvaluationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    public DoctorEvaluation createDoctorEvaluation(Long doctorId, Long patientId, DoctorEvaluation evaluation) {
        // Check if doctor and patient exist
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));

        // Set the doctor and patient
        evaluation.setDoctor(doctor);
        evaluation.setPatient(patient);

        // Save and return the evaluation
        return doctorEvaluationRepository.save(evaluation);
    }

    public List<DoctorEvaluation> getDoctorEvaluations(Long doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDoctorEvaluations'");
    }
}
