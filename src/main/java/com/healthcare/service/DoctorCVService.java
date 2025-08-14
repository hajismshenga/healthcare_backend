package com.healthcare.service;

import com.healthcare.dto.DoctorCVResponse;
import com.healthcare.exception.DoctorNotFoundException;
import com.healthcare.model.Doctor;
import com.healthcare.repository.DoctorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DoctorCVService {

    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorCVService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorCVResponse> getAllDoctorsCV() {
        log.info("Fetching CV information for all doctors");
        
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .map(this::convertToDoctorCVResponse)
                .collect(Collectors.toList());
    }

    public DoctorCVResponse getDoctorCV(String doctorId) {
        log.info("Fetching CV information for doctor: {}", doctorId);
        
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));
        
        return convertToDoctorCVResponse(doctor);
    }

    public List<DoctorCVResponse> getDoctorsByProfession(String profession) {
        log.info("Fetching CV information for doctors with profession: {}", profession);
        
        List<Doctor> doctors = doctorRepository.findByProfession(profession);
        return doctors.stream()
                .map(this::convertToDoctorCVResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorCVResponse> getAvailableDoctors() {
        log.info("Fetching CV information for available doctors");
        
        List<Doctor> doctors = doctorRepository.findAll(); // In a real system, you'd filter by availability
        return doctors.stream()
                .map(this::convertToDoctorCVResponse)
                .collect(Collectors.toList());
    }

    private DoctorCVResponse convertToDoctorCVResponse(Doctor doctor) {
        return DoctorCVResponse.builder()
                .doctorId(doctor.getDoctorId())
                .name(doctor.getName())
                .profession(doctor.getProfession())
                .hospitalName(doctor.getHospital() != null ? doctor.getHospital().getName() : "N/A")
                .hospitalLocation(doctor.getHospital() != null ? doctor.getHospital().getLocation() : "N/A")
                .qualifications("MBBS, MD") // This could be stored in the Doctor model
                .specializations(doctor.getProfession()) // Using profession as specialization for now
                .yearsOfExperience(5) // This could be calculated or stored
                .licenseExpiryDate(null) // This could be stored in the Doctor model
                .contactNumber("N/A") // This could be stored in the Doctor model
                .email("N/A") // This could be stored in the Doctor model
                .availabilitySchedule("Monday-Friday, 9 AM - 5 PM") // This could be stored
                .isAvailableForNewPatients(true) // This could be stored
                .averageRating(4.5) // This could be calculated from reviews
                .totalReviews(10) // This could be calculated from reviews
                .bio("Experienced " + doctor.getProfession() + " with expertise in patient care.") // This could be stored
                .languages(List.of("English", "Swahili")) // This could be stored
                .consultationFee("50,000 TZS") // This could be stored
                .build();
    }
}
