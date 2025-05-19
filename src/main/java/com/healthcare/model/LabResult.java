package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resultDetails;
    private String interpretation;
    private String normalRange;
    private String notes;
    private String recommendations;
    private LocalDateTime resultDate;

    @OneToOne(mappedBy = "labResult", cascade = CascadeType.ALL)
    private LabTest labTest;
}
