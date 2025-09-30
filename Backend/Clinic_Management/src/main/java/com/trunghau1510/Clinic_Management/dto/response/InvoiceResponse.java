package com.trunghau1510.Clinic_Management.dto.response;

import com.trunghau1510.Clinic_Management.entity.enums.InvoiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponse {

    String id;
    String appointmentId;
    BigDecimal amount;
    Instant issueDate;
    LocalDate dueDate;
    InvoiceStatus status;
    Instant createdAt;
    Instant updatedAt;
}
