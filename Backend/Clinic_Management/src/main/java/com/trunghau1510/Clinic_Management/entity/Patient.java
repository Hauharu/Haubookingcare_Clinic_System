package com.trunghau1510.Clinic_Management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @Column(name = "id")
    String id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id")
    User user;

    @Lob
    @Column(name = "medical_history")
    String medicalHistory;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    Set<Appointment> appointments;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    Set<HealthRecord> healthRecords;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    Set<Prescription> prescriptions;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    Set<Review> reviews;
}