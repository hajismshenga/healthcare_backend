package com.healthcare.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "laboratories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Laboratory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;
}
