package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.VNPayCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.VNPayResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {

    VNPayResponse createPayment(VNPayCreationRequest request, HttpServletRequest httpRequest);
    String handlePaymentReturn(HttpServletRequest request);
}
