package com.trunghau1510.Clinic_Management.entity;

import com.trunghau1510.Clinic_Management.entity.enums.PaymentMethod;
import com.trunghau1510.Clinic_Management.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    Invoice invoice;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    PaymentMethod paymentMethod;

    @Column(name = "transaction_id", unique = true)
    String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    LocalDateTime paymentDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    String notes;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.paymentDate == null) {
            this.paymentDate = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}