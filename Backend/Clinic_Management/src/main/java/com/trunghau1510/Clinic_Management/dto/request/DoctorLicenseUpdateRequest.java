package com.trunghau1510.Clinic_Management.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorLicenseUpdateRequest {

    @Size(max = 100, message = "Số giấy phép không được vượt quá 100 ký tự")
    String licenseNumber;

    @Size(max = 255, message = "Nơi cấp giấy phép không được vượt quá 255 ký tự")
    String issuingAuthority;

    LocalDate issueDate;

    LocalDate expiryDate;

    @Size(max = 1000, message = "Phạm vi mô tả không được vượt quá 1000 ký tự")
    String scopeDescription;

    Boolean isVerified;

    LocalDate verificationDate;
}
