package com.trunghau1510.Clinic_Management.entity;

import com.trunghau1510.Clinic_Management.entity.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    Appointment appointment;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @Column(name = "issue_date")
    Instant issueDate;

    @Column(name = "due_date")
    LocalDate dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    InvoiceStatus status;

    @Column(name = "created_at")
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    Set<Payment> payments;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.issueDate == null) {
            this.issueDate = now;
        }
        if (this.status == null) {
            this.status = InvoiceStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}