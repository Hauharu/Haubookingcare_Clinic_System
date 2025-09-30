package com.trunghau1510.Clinic_Management.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionCreationRequest {

    String healthRecordId;

    String medicationId;

    @Size(max = 100, message = "Liều lượng không được vượt quá 100 ký tự")
    String dosage;

    @Size(max = 100, message = "Tần suất sử dụng không được vượt quá 100 ký tự")
    String frequency;

    @Size(max = 1000, message = "Hướng dẫn sử dụng không được vượt quá 1000 ký tự")
    String instructions;

    String doctorId;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    String notes;
}
