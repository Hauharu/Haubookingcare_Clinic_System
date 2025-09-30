package com.trunghau1510.Clinic_Management.entity;

import lombok.*;
import java.util.Set;
import java.time.Instant;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "health_record")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @NotNull
    @Column(name = "record_date", nullable = false)
    LocalDate recordDate;

    @Lob
    @Column(name = "symptoms")
    String symptoms;

    @Lob
    @Column(name = "diagnosis")
    String diagnosis;

    @Lob
    @Column(name = "notes")
    String notes;

    @Column(name = "created_at")
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;

    @OneToMany(mappedBy = "healthRecord", cascade = CascadeType.ALL)
    Set<Prescription> prescriptions;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.recordDate == null) {
            this.recordDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}