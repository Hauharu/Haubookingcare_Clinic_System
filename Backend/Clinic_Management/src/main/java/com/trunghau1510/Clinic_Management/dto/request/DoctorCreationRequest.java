package com.trunghau1510.Clinic_Management.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorCreationRequest extends UserCreationRequest {

    String specialtyId;
    Integer yearsExperience;
    String biography;
    BigDecimal consultationFee;
}