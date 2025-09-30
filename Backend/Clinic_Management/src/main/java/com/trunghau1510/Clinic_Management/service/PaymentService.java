package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.PaymentCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse addPayment(PaymentCreationRequest request);
    PaymentResponse getPaymentById(String id);
    List<PaymentResponse> getPaymentsByInvoiceId(String invoiceId);
    PaymentResponse getPaymentByTransactionId(String transactionId);
}
