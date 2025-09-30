package com.trunghau1510.Clinic_Management.dto.request;

import lombok.Data;

@Data
public class PaypalCreationRequest {

    private Double amount;
    private String currency;
    private String invoiceId;
    private Long paymentId;
    private String description;
}
