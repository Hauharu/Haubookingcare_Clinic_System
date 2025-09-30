package com.trunghau1510.Clinic_Management.entity;

import lombok.*;
import java.time.Instant;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "invalidated_token")
public class InvalidatedToken {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @Column(name = "expiry_time", nullable = false)
    Instant expiryTime;
}