package com.healthcare.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospitals")
@Data  // Lombok annotation for getters/setters
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String location;
}
