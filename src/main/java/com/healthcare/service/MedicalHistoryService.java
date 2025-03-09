package com.healthcare.service;

import com.healthcare.model.MedicalHistory;
import com.healthcare.model.Appointment;
import com.healthcare.repository.MedicalHistoryRepository;
import com.healthcare.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Create medical history linked to an appointment
    public MedicalHistory createMedicalHistory(Long appointmentId, MedicalHistory medicalHistory) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        medicalHistory.setAppointment(appointment);
        return medicalHistoryRepository.save(medicalHistory);
    }
}
