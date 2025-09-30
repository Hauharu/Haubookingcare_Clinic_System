package com.trunghau1510.Clinic_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
@Table(name = "medicine_prescription")
public class MedicinePrescription {

    @EmbeddedId
    MedicinePrescriptionId id;

    @MapsId("prescriptionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    Prescription prescription;

    @MapsId("medicineId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    Medicine medicine;

    @NotNull
    @Min(1)
    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @NotNull
    @Lob
    @Column(name = "dosage_instruction", nullable = false)
    String dosageInstruction;
}