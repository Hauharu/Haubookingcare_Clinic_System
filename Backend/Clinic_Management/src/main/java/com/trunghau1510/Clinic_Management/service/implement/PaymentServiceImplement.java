package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.dto.request.PaymentCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.PaymentResponse;
import com.trunghau1510.Clinic_Management.entity.Payment;
import com.trunghau1510.Clinic_Management.entity.Invoice;
import com.trunghau1510.Clinic_Management.entity.enums.PaymentStatus;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.mapper.PaymentMapper;
import com.trunghau1510.Clinic_Management.repository.PaymentRepository;
import com.trunghau1510.Clinic_Management.repository.InvoiceRepository;
import com.trunghau1510.Clinic_Management.service.PaymentService;
import com.trunghau1510.Clinic_Management.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImplement implements PaymentService {

    PaymentRepository paymentRepository;
    InvoiceRepository invoiceRepository;
    PaymentMapper paymentMapper;
    EmailService emailService;

    @Override
    public PaymentResponse addPayment(PaymentCreationRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        Payment payment = paymentMapper.toEntity(request, invoice);
        // Set thời gian thanh toán nếu là thanh toán thành công
        if ("OTHER".equalsIgnoreCase(request.getPaymentMethod()) ||
            "CASH".equalsIgnoreCase(request.getPaymentMethod()) ||
            "BANK_TRANSFER".equalsIgnoreCase(request.getPaymentMethod()) ||
            "VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
        }
        Payment savedPayment = paymentRepository.save(payment);
        if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod()) && payment.getStatus() == PaymentStatus.COMPLETED) {
            String toEmail = invoice.getAppointment().getPatient().getUser().getEmail();
            String patientName = invoice.getAppointment().getPatient().getUser().getFirstName() + " " + invoice.getAppointment().getPatient().getUser().getLastName();
            String amount = payment.getAmountPaid() != null ? payment.getAmountPaid().toString() : "";
            String transactionId = payment.getTransactionId();
            emailService.sendPaymentSuccessEmail(toEmail, patientName, amount, transactionId);
        }
        return paymentMapper.toResponseDTO(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        return paymentMapper.toResponseDTO(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByInvoiceId(String invoiceId) {
        List<Payment> payments = paymentRepository.findByInvoiceId(invoiceId);

        return payments.stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElse(null);

        return payment != null ? paymentMapper.toResponseDTO(payment) : null;
    }
}
