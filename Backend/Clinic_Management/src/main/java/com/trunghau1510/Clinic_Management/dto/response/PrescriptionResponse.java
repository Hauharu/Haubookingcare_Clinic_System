package com.trunghau1510.Clinic_Management.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionResponse {
    String id;
    String medicationName;
    String dosage;
    String frequency;
    String instructions;
    LocalDate issueDate;
    String doctorId;
    String patientId;
}
