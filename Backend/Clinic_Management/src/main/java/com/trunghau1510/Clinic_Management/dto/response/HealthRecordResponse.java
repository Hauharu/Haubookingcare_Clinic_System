package com.trunghau1510.Clinic_Management.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthRecordResponse {

    String id;
    UserResponse patientResponse;
    UserResponse doctorResponse;
    LocalDate recordDate;
    String symptoms;
    String diagnosis;
    String notes;
    Instant createdAt;
    Instant updatedAt;
    String appointmentTime;
    List<PrescriptionResponse> prescriptions;
}
