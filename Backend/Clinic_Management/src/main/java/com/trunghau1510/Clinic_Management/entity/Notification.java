package com.trunghau1510.Clinic_Management.entity;

import com.trunghau1510.Clinic_Management.entity.enums.NotificationType;
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
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    String title;

    @NotNull
    @Column(name = "content", nullable = false)
    String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    NotificationType type;

    @Column(name = "is_read")
    boolean isRead;

    @Column(name = "created_at")
    Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}