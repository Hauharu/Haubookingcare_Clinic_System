package com.trunghau1510.Clinic_Management.entity;

import com.trunghau1510.Clinic_Management.entity.enums.Gender;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @NotNull
    @Size(max = 100)
    @Column(name = "first_name", nullable = false)
    String firstName;

    @NotNull
    @Size(max = 100)
    @Column(name = "last_name", nullable = false)
    String lastName;

    @NotNull
    @Size(max = 100)
    @Column(name = "username", nullable = false, unique = true)
    String username;

    @NotNull
    @Size(max = 255)
    @Column(name = "password", nullable = false)
    String password;

    @NotNull
    @Size(max = 255)
    @Column(name = "email", nullable = false, unique = true)
    String email;

    @NotNull
    @Size(max = 20)
    @Column(name = "phone_number", nullable = false, unique = true)
    String phoneNumber;

    @NotNull
    @Lob
    @Column(name = "address", nullable = false)
    String address;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    Gender gender;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    Role role;

    @Size(max = 255)
    @Column(name = "avatar")
    String avatar;


    @Column(name = "is_active")
    Boolean isActive;

    @Column(name = "created_at")
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    Doctor doctor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    Patient patient;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    Set<HealthRecord> healthRecords;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isActive == null) {
            this.isActive = true;
        }
        if ((this.avatar == null) || this.avatar.isEmpty()) {
            this.avatar = "https://res.cloudinary.com/dwwfgtxv4/image/upload/v1754280542/anh-dai-dien_kz9gpm.png";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}