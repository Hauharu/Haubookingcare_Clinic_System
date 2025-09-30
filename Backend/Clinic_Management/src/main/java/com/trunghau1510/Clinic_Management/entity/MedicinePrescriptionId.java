package com.trunghau1510.Clinic_Management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class MedicinePrescriptionId implements Serializable {

    @Column(name = "prescription_id")
    String prescriptionId;

    @Column(name = "medicine_id")
    String medicineId;
}