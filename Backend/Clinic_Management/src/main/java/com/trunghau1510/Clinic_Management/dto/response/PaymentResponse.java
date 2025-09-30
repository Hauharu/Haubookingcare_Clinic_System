package com.trunghau1510.Clinic_Management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {

    String id;
    String invoiceId;
    BigDecimal amountPaid;
    String paymentMethod;
    String transactionId;
    String status;
    String notes;
    String paymentUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime paymentDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}
