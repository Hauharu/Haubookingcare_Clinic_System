package com.trunghau1510.Clinic_Management.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineUpdateRequest {

    String id;
    String name;
    String description;
    String unit;
    BigDecimal pricePerUnit;
}
