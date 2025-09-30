package com.trunghau1510.Clinic_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "time_slot")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    Doctor doctor;

    @NotNull
    @Column(name = "slot_date", nullable = false)
    LocalDate slotDate;

    @NotNull
    @Column(name = "start_time", nullable = false)
    LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    LocalTime endTime;

    @Column(name = "is_booked")
    Boolean isBooked;

    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.isBooked == null) {
            this.isBooked = false;
        }
    }
}
