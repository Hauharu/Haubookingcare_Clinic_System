package com.trunghau1510.Clinic_Management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorResponse {

    String id;
    UserResponse user;
    SpecialtyResponse specialty;
    Integer yearsExperience;
    String biography;
    BigDecimal consultationFee;
    BigDecimal averageRating;
    String avatar;

}