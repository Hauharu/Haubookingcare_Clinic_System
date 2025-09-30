package com.trunghau1510.Clinic_Management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorLicenseResponse {

    String id;
    DoctorResponse doctor;
    String licenseNumber;
    String issuingAuthority;
    LocalDate issueDate;
    LocalDate expiryDate;
    String scopeDescription;
    Boolean isVerified;
    LocalDate verificationDate;
    Instant createdAt;
    Instant updatedAt;
}
