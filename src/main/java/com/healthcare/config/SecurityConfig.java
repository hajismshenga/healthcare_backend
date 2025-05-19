package com.healthcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/api/hospitals/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/doctors/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/laboratories/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/patients/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/clinical-records/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/second-opinions/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/lab-tests/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/lab-results/**")).permitAll()
                .anyRequest().permitAll()
            )
            .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
