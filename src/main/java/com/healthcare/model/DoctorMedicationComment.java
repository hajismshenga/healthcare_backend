package com.healthcare.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctor_medication_comment", schema = "healthcare")
public class DoctorMedicationComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comment_id", unique = true, nullable = false, updatable = false)
    private String commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Patient patient;

    @Column(name = "comment", columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type", nullable = false)
    private CommentType commentType;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "side_effects", columnDefinition = "TEXT")
    private String sideEffects;

    @Column(name = "dosage_adjustments", columnDefinition = "TEXT")
    private String dosageAdjustments;

    @Column(name = "alternative_medications", columnDefinition = "TEXT")
    private String alternativeMedications;

    @Column(name = "comment_date", nullable = false)
    @Builder.Default
    private LocalDateTime commentDate = LocalDateTime.now();

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    public enum CommentType {
        GENERAL_REVIEW,
        DOSAGE_ADJUSTMENT,
        SIDE_EFFECT_ALERT,
        ALTERNATIVE_SUGGESTION,
        INTERACTION_WARNING,
        COMPLIANCE_REMINDER
    }
}
