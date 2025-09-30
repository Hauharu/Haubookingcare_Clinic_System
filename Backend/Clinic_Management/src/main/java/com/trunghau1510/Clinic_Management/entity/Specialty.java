package com.trunghau1510.Clinic_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "specialty")
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Lob
    @Column(name = "description")
    String description;

    @OneToMany(mappedBy = "specialty", fetch = FetchType.LAZY)
    Set<Doctor> doctors;
}
