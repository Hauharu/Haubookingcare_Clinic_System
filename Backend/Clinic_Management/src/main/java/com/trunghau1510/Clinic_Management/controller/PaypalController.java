package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.PaypalCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.PaypalResponse;
import com.trunghau1510.Clinic_Management.service.PaypalService;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/paypal")
public class PaypalController {
    PaypalService paypalService;

    @PostMapping("/create-payment")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PaypalResponse> createPayment(@RequestBody PaypalCreationRequest request) {
        String returnUrl = "http://localhost:8080/Clinic-Haubookingcare/api/paypal/success?invoiceId=" + request.getInvoiceId();
        String cancelUrl = "http://localhost:8080/Clinic-Haubookingcare/api/paypal/cancel?invoiceId=" + request.getInvoiceId();
        PaypalResponse response = paypalService.createOrder(request, returnUrl, cancelUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public RedirectView paymentSuccess(@RequestParam(required = false) String token, @RequestParam(required = false) String invoiceId) {
        boolean isSuccess = false;
        if (token != null) {
            isSuccess = paypalService.captureOrder(token, invoiceId);
        }
        String redirectUrl;
        if (invoiceId != null) {
            // Redirect về trang chi tiết hóa đơn giống VNPay
            redirectUrl = "http://localhost:3000/invoice/" + invoiceId;
        } else {
            redirectUrl = "http://localhost:3000/";
        }
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/cancel")
    public RedirectView paymentCancel(@RequestParam(required = false) String invoiceId) {
        String redirectUrl;
        if (invoiceId != null) {
            redirectUrl = "http://localhost:3000/invoice/" + invoiceId;
        } else {
            redirectUrl = "http://localhost:3000/";
        }
        return new RedirectView(redirectUrl);
    }
}
