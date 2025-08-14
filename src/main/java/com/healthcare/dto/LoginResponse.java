package com.healthcare.dto;

import com.healthcare.model.Hospital;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private String doctorId;
    private String hospitalId;
    private String laboratoryId;
    private String patientId;
    private String name;
    private String role;
    private String message;
    private boolean success;
    
    public boolean isSuccess() {
        return success;
    }

    public static LoginResponse fromHospital(Hospital hospital, String token) {
        return LoginResponse.builder()
                .success(true)
                .token(token)
                .id(hospital.getId())
                .hospitalId(hospital.getHospitalId())
                .name(hospital.getName())
                .role("HOSPITAL")
                .message("Login successful")
                .build();
    }

    public static LoginResponse error(String message) {
        return LoginResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
    
    public static LoginResponse fromDoctor(com.healthcare.model.Doctor doctor, String token) {
        return LoginResponse.builder()
                .success(true)
                .token(token)
                .id(doctor.getId())
                .doctorId(doctor.getDoctorId())
                .name(doctor.getName())
                .hospitalId(doctor.getHospital() != null ? doctor.getHospital().getHospitalId() : null)
                .role("DOCTOR")
                .message("Login successful")
                .build();
    }
    
    public static LoginResponse fromLaboratory(com.healthcare.model.Laboratory laboratory, String token) {
        return LoginResponse.builder()
                .success(true)
                .token(token)
                .id(laboratory.getId())
                .laboratoryId(laboratory.getLaboratoryId())
                .name(laboratory.getName())
                .hospitalId(laboratory.getHospital() != null ? laboratory.getHospital().getHospitalId() : null)
                .role("LAB")
                .message("Login successful")
                .build();
    }
    
    public static LoginResponse fromPatient(com.healthcare.model.Patient patient, String token) {
        return LoginResponse.builder()
                .success(true)
                .token(token)
                .id(patient.getId())
                .patientId(patient.getPatientId())
                .name(patient.getName())
                .doctorId(patient.getDoctor() != null ? patient.getDoctor().getDoctorId() : null)
                .hospitalId(patient.getHospital() != null ? patient.getHospital().getHospitalId() : null)
                .role("PATIENT")
                .message("Login successful")
                .build();
    }
}
