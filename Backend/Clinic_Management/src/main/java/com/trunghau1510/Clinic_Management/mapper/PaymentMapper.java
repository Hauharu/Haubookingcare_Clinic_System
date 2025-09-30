package com.trunghau1510.Clinic_Management.mapper;

import com.trunghau1510.Clinic_Management.dto.request.PaymentCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.PaymentResponse;
import com.trunghau1510.Clinic_Management.entity.Payment;
import com.trunghau1510.Clinic_Management.entity.Invoice;
import com.trunghau1510.Clinic_Management.entity.enums.PaymentMethod;
import com.trunghau1510.Clinic_Management.entity.enums.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "paymentStatusToString")
    @Mapping(target = "paymentUrl", ignore = true)
    PaymentResponse toResponseDTO(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoice", source = "invoice")
    @Mapping(target = "amountPaid", source = "request.amount")
    @Mapping(target = "paymentMethod", source = "request.paymentMethod", qualifiedByName = "stringToPaymentMethod")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "transactionId", expression = "java(generateTransactionId(request.getInvoiceId()))")
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Payment toEntity(PaymentCreationRequest request, Invoice invoice);

    @Named("paymentMethodToString")
    default String paymentMethodToString(PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : null;
    }

    @Named("paymentStatusToString")
    default String paymentStatusToString(PaymentStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToPaymentMethod")
    default PaymentMethod stringToPaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return PaymentMethod.OTHER;
        }

        // Chỉ VNPAY thì trả về VNPAY, còn lại tất cả đều là OTHER
        if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
            return PaymentMethod.VNPAY;
        } else {
            return PaymentMethod.OTHER;
        }
    }

    default String generateTransactionId(String invoiceId) {
        String prefix = "PAY_";
        String invoicePart = "UNKNOWN";
        if (invoiceId != null && invoiceId.length() >= 8) {
            invoicePart = invoiceId.substring(0, 8);
        } else if (invoiceId != null) {
            invoicePart = invoiceId;
        }
        return prefix + invoicePart + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis();
    }
}
