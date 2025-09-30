package com.trunghau1510.Clinic_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    Doctor doctor;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    Integer rating;

    @Lob
    @Column(name = "comment")
    String comment;

    @Column(name = "review_date", nullable = false)
    Instant reviewDate;

    @Lob
    @Column(name = "doctor_response")
    String doctorResponse;

    @Column(name = "response_date")
    Instant responseDate;

    @Column(name = "is_visible")
    Boolean isVisible;

    @PrePersist
    protected void onCreate() {
        if (this.reviewDate == null) {
            this.reviewDate = Instant.now();
        }
        if (this.isVisible == null) {
            this.isVisible = true;
        }
    }
}