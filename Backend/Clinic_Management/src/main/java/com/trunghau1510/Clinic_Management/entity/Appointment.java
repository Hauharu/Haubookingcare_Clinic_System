package com.trunghau1510.Clinic_Management.entity;

import lombok.*;
import java.util.Set;
import java.time.Instant;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import com.trunghau1510.Clinic_Management.entity.enums.Status;
import com.trunghau1510.Clinic_Management.entity.enums.ConsultationType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    Doctor doctor;

    @NotNull
    @Column(name = "appointment_time", nullable = false)
    LocalDateTime appointmentTime;

    @Column(name = "duration_minutes")
    Integer durationMinutes;

    @Lob
    @Column(name = "reason")
    String reason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_type")
    ConsultationType consultationType;

    @Size(max = 255)
    @Column(name = "video_call_link")
    String videoCallLink;

    @Lob
    @Column(name = "cancellation_reason")
    String cancellationReason;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    Invoice invoice;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    Set<HealthRecord> healthRecords;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    Review review;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.durationMinutes == null) {
            this.durationMinutes = 30;
        }
        if (this.status == null) {
            this.status = Status.SCHEDULED;
        }
        if (this.consultationType == null) {
            this.consultationType = ConsultationType.OFFLINE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}