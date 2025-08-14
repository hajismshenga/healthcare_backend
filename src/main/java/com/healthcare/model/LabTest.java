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
@Table(name = "lab_test", schema = "healthcare")
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "test_id", unique = true, nullable = false, updatable = false)
    private String testId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratory_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Laboratory laboratory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Doctor doctor;

    @NotBlank(message = "Test requirement is required")
    @Size(min = 10, max = 500, message = "Test requirement must be between 10 and 500 characters")
    @Column(name = "test_requirement", columnDefinition = "TEXT")
    private String testRequirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private TestStatus status = TestStatus.PENDING;

    @Column(name = "requested_date")
    @Builder.Default
    private LocalDateTime requestedDate = LocalDateTime.now();

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "test_result", columnDefinition = "TEXT")
    private String testResult;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum TestStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
