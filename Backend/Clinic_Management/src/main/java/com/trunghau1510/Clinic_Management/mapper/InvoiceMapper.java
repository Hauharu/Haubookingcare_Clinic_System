package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.InvoiceCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.InvoiceUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.InvoiceResponse;
import com.trunghau1510.Clinic_Management.entity.Appointment;
import com.trunghau1510.Clinic_Management.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", source = "appointment")
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "issueDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    Invoice toInvoice(InvoiceCreationRequest request, Appointment appointment);

    @Mapping(target = "appointmentId", source = "appointment.id")
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "issueDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    void updateInvoice(@MappingTarget Invoice invoice, InvoiceUpdateRequest request);
}
