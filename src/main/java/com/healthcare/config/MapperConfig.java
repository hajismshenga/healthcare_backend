package com.healthcare.config;

import com.healthcare.mapper.HospitalMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    
    @Bean
    public HospitalMapper hospitalMapper() {
        return new HospitalMapper();
    }
}
