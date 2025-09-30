package com.trunghau1510.Clinic_Management.entity;

import lombok.*;
import java.time.Instant;
import java.time.LocalTime;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotNull;
import com.trunghau1510.Clinic_Management.entity.enums.DayOfWeek;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "doctor_schedule")
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    Doctor doctor;

    @NotNull
    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    DayOfWeek dayOfWeek;

    @NotNull
    @Column(name = "start_time", nullable = false)
    LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    LocalTime endTime;

    @Column(name = "is_available")
    Boolean isAvailable;

    @Column(name = "created_at")
    Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.isAvailable == null) {
            this.isAvailable = true;
        }
    }
}