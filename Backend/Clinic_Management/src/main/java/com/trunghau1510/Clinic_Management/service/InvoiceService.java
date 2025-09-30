package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.request.InvoiceCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.InvoiceUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse addInvoice(InvoiceCreationRequest request);
    InvoiceResponse updateInvoice(String id, InvoiceUpdateRequest request);
    InvoiceResponse getInvoiceById(String id);
    InvoiceResponse getInvoiceByAppointmentId(String appointmentId);
    List<InvoiceResponse> getInvoicesByUserId(String userId);
    InvoiceResponse updateInvoiceStatus(String id, String status);
}
