package com.trunghau1510.Clinic_Management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineResponse {

    String id;
    String name;
    String description;
    String unit;
    BigDecimal pricePerUnit;
}
