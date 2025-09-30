package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.VNPayCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.VNPayResponse;
import com.trunghau1510.Clinic_Management.entity.Payment;
import com.trunghau1510.Clinic_Management.repository.PaymentRepository;
import com.trunghau1510.Clinic_Management.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayController {

    PaymentRepository paymentRepository;
    VNPayService vnPayService;

    @PostMapping("/create-payment")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<VNPayResponse>> createPayment(
            @Valid @RequestBody VNPayCreationRequest request,
            HttpServletRequest httpServletRequest) {
        VNPayResponse vnPayResponse = vnPayService.createPayment(request, httpServletRequest);
        return ResponseEntity.ok(ApiResponse.<VNPayResponse>builder()
                .result(vnPayResponse)
                .message("Payment URL created successfully")
                .build());
    }

    @GetMapping("/return")
    public RedirectView paymentReturn(HttpServletRequest request) {
        String result = vnPayService.handlePaymentReturn(request);

        // Truy vấn payment theo vnp_TxnRef
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String invoiceId = null;
        if (vnp_TxnRef != null) {
            Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(vnp_TxnRef);
            if (paymentOpt.isPresent()) {
                invoiceId = paymentOpt.get().getInvoice().getId();
            }
        }
        String redirectUrl;
        if ("SUCCESS".equals(result) && invoiceId != null) {
            redirectUrl = "http://localhost:3000/invoice/" + invoiceId;
        } else if ("FAILED".equals(result) && invoiceId != null) {
            redirectUrl = "http://localhost:3000/invoice/" + invoiceId;
        } else {
            redirectUrl = "http://localhost:3000/";
        }

        return new RedirectView(redirectUrl);
    }

    @GetMapping("/payment-status/{txnRef}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<String>> getPaymentStatus(@PathVariable String txnRef) {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .result("Payment status for transaction: " + txnRef)
                .message("Payment status retrieved successfully")
                .build());
    }
}