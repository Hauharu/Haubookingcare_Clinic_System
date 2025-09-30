package com.trunghau1510.Clinic_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    String id;

    @NotNull
    @Size(max = 150)
    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Lob
    @Column(name = "description")
    String description;

    @NotNull
    @Size(max = 50)
    @Column(name = "unit", nullable = false)
    String unit;

    @Column(name = "price_per_unit")
    BigDecimal pricePerUnit;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL)
    Set<MedicinePrescription> medicinePrescriptions;

    @PrePersist
    protected void onCreate() {
        if (this.pricePerUnit == null) {
            this.pricePerUnit = BigDecimal.ZERO;
        }
    }
}