package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.PaymentCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.PaymentResponse;
import com.trunghau1510.Clinic_Management.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiPaymentController {

    PaymentService paymentService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ApiResponse<PaymentResponse> getPaymentById(@PathVariable String id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ApiResponse.<PaymentResponse>builder()
                .result(payment)
                .message("Lấy thông tin thanh toán thành công")
                .build();
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ApiResponse<List<PaymentResponse>> getPaymentsByInvoiceId(@PathVariable String invoiceId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByInvoiceId(invoiceId);
        return ApiResponse.<List<PaymentResponse>>builder()
                .result(payments)
                .message("Lấy danh sách thanh toán theo hóa đơn thành công")
                .count(payments.size())
                .build();
    }

    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ApiResponse<PaymentResponse> getPaymentByTransactionId(@PathVariable String transactionId) {
        PaymentResponse payment = paymentService.getPaymentByTransactionId(transactionId);
        return ApiResponse.<PaymentResponse>builder()
                .result(payment)
                .message("Lấy thông tin thanh toán theo mã giao dịch thành công")
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<PaymentResponse> createPayment(@RequestBody PaymentCreationRequest request) {
        PaymentResponse payment = paymentService.addPayment(request);
        return ApiResponse.<PaymentResponse>builder()
                .result(payment)
                .message("Tạo thanh toán thành công")
                .build();
    }

}
