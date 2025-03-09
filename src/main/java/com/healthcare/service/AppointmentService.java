package com.healthcare.service;

import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    // Create a new appointment
    public Appointment createAppointment(Long doctorId, Long patientId, String reason, String appointmentTime) {
        // Parse the appointment time string to LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentTime, formatter);

        // Retrieve doctor and patient from the database
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));

        // Create and save the appointment
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setReason(reason);
        appointment.setAppointmentTime(appointmentDateTime);

        return appointmentRepository.save(appointment);
    }
}
