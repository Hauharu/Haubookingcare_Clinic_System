package com.trunghau1510.Clinic_Management.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthRecordCreationRequest {

    String patientId;

    String appointmentId;

    LocalDate recordDate;

    @Size(max = 5000, message = "Triệu chứng không được vượt quá 5000 ký tự")
    String symptoms;

    @Size(max = 5000, message = "Chẩn đoán không được vượt quá 5000 ký tự")
    String diagnosis;

    @Size(max = 5000, message = "Ghi chú không được vượt quá 5000 ký tự")
    String notes;

//    List<PrescriptionCreationRequest> prescriptions;
}
