package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.dto.request.InvoiceCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.InvoiceUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.InvoiceResponse;
import com.trunghau1510.Clinic_Management.entity.Appointment;
import com.trunghau1510.Clinic_Management.entity.Invoice;
import com.trunghau1510.Clinic_Management.entity.enums.InvoiceStatus;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.mapper.InvoiceMapper;
import com.trunghau1510.Clinic_Management.repository.AppointmentRepository;
import com.trunghau1510.Clinic_Management.repository.InvoiceRepository;
import com.trunghau1510.Clinic_Management.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImplement implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    public InvoiceResponse addInvoice(InvoiceCreationRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
            .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_EXISTS));
        Invoice invoice = invoiceMapper.toInvoice(request, appointment);
        invoice.setStatus(request.getStatus() != null ? request.getStatus() : InvoiceStatus.PENDING);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(savedInvoice);
    }

    @Override
    public InvoiceResponse updateInvoice(String id, InvoiceUpdateRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_EXISTS));

        invoiceMapper.updateInvoice(invoice, request);
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(updatedInvoice);
    }

    @Override
    public InvoiceResponse getInvoiceById(String id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_EXISTS));
        return invoiceMapper.toInvoiceResponse(invoice);
    }

    @Override
    public InvoiceResponse getInvoiceByAppointmentId(String appointmentId) {
        Invoice invoice = invoiceRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_EXISTS));
        return invoiceMapper.toInvoiceResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getInvoicesByUserId(String userId) {
        List<Invoice> invoices = invoiceRepository.findByAppointmentPatientIdOrAppointmentDoctorId(userId, userId);
        return invoices.stream()
            .map(invoiceMapper::toInvoiceResponse)
            .collect(Collectors.toList());
    }

    public InvoiceResponse updateInvoiceStatus(String id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_EXISTS));
        invoice.setStatus(InvoiceStatus.valueOf(status));
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(updatedInvoice);
    }
}
