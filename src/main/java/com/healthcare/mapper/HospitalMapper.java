package com.healthcare.mapper;

import com.healthcare.dto.HospitalDTO;
import com.healthcare.model.Hospital;

public class HospitalMapper {
    
    public HospitalDTO toDTO(Hospital hospital) {
        if (hospital == null) {
            return null;
        }
        
        return HospitalDTO.builder()
                .id(hospital.getId())
                .hospitalId(hospital.getHospitalId())
                .name(hospital.getName())
                .ownership(hospital.getOwnership())
                .district(hospital.getDistrict())
                .location(hospital.getLocation())
                .contactInfo(hospital.getContactInfo())
                .username(hospital.getUser() != null ? hospital.getUser().getUsername() : null)
                .role(hospital.getUser() != null ? hospital.getUser().getRole() : null)
                .build();
    }
}
