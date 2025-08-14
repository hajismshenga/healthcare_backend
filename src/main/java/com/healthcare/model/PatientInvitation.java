package com.healthcare.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patient_invitation", schema = "healthcare")
public class PatientInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "invitation_id", unique = true, nullable = false, updatable = false)
    private String invitationId;

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

    @Column(name = "invitation_message", columnDefinition = "TEXT", nullable = false)
    private String invitationMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false)
    private UrgencyLevel urgencyLevel;

    @Column(name = "preferred_consultation_date")
    private String preferredConsultationDate;

    @Column(name = "preferred_consultation_time")
    private String preferredConsultationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "doctor_response", columnDefinition = "TEXT")
    private String doctorResponse;

    @Column(name = "invitation_date", nullable = false)
    @Builder.Default
    private LocalDateTime invitationDate = LocalDateTime.now();

    @Column(name = "response_date")
    private LocalDateTime responseDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public enum UrgencyLevel {
        LOW, MEDIUM, HIGH, EMERGENCY
    }

    public enum InvitationStatus {
        PENDING, ACCEPTED, DECLINED, EXPIRED
    }
}
