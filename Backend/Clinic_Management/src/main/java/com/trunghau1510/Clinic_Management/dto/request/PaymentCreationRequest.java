package com.trunghau1510.Clinic_Management.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCreationRequest {

    String invoiceId;
    BigDecimal amount;
    String paymentMethod;
    String orderInfo;
    String notes;
}
