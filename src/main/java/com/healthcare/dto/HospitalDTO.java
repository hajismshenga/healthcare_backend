package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO {
    private Long id;
    private String hospitalId;
    private String name;
    private String ownership;
    private String district;
    private String location;
    private String contactInfo;
    private String username;
    private String role;
}
