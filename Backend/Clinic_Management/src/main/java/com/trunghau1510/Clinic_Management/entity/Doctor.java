package com.trunghau1510.Clinic_Management.entity;

import lombok.*;
import java.util.Set;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.DecimalMin;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    Specialty specialty;

    @Column(name = "years_experience")
    Integer yearsExperience;

    @Lob
    @Column(name = "biography")
    String biography;

    @DecimalMin("0.00")
    @Column(name = "consultation_fee")
    BigDecimal consultationFee;

    @DecimalMin("0.00")
    @Column(name = "average_rating")
    BigDecimal averageRating;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    Set<Appointment> appointments;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    Set<DoctorSchedule> doctorSchedules;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    Set<DoctorLicense> doctorLicenses;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    Set<TimeSlot> timeSlots;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    Set<Review> reviews;

    @PrePersist
    protected void onCreate() {
        if (this.yearsExperience == null) this.yearsExperience = 0;
        if (this.consultationFee == null) this.consultationFee = BigDecimal.ZERO;
        if (this.averageRating == null) this.averageRating = BigDecimal.ZERO;
    }
}