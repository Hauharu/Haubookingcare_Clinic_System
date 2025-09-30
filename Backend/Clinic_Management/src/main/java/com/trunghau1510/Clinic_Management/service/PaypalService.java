package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.PaypalCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.PaypalResponse;

public interface PaypalService {

    PaypalResponse createOrder(PaypalCreationRequest request, String returnUrl, String cancelUrl);
    boolean captureOrder(String token, String invoiceId);
}

