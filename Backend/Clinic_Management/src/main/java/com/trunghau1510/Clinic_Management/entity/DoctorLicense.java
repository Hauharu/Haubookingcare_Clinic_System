package com.trunghau1510.Clinic_Management.entity;

import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "doctor_license")
public class DoctorLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    Doctor doctor;

    @NotNull
    @Size(max = 100)
    @Column(name = "license_number", nullable = false, unique = true)
    String licenseNumber;

    @NotNull
    @Size(max = 255)
    @Column(name = "issuing_authority", nullable = false)
    String issuingAuthority;

    @NotNull
    @Column(name = "issue_date", nullable = false)
    LocalDate issueDate;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @Lob
    @Column(name = "scope_description")
    String scopeDescription;

    @Column(name = "is_verified")
    Boolean isVerified;

    @Column(name = "verification_date")
    LocalDate verificationDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isVerified == null) {
            this.isVerified = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}