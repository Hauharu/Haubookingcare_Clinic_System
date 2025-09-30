package com.trunghau1510.Clinic_Management.service.implement;

import com.paypal.orders.*;
import com.paypal.http.HttpResponse;
import com.paypal.core.PayPalHttpClient;
import com.trunghau1510.Clinic_Management.config.PaypalConfig;
import com.trunghau1510.Clinic_Management.dto.request.PaypalCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.PaypalResponse;
import com.trunghau1510.Clinic_Management.entity.Invoice;
import com.trunghau1510.Clinic_Management.entity.Payment;
import com.trunghau1510.Clinic_Management.entity.enums.InvoiceStatus;
import com.trunghau1510.Clinic_Management.entity.enums.PaymentMethod;
import com.trunghau1510.Clinic_Management.entity.enums.PaymentStatus;
import com.trunghau1510.Clinic_Management.repository.InvoiceRepository;
import com.trunghau1510.Clinic_Management.repository.PaymentRepository;
import com.trunghau1510.Clinic_Management.service.PaypalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaypalServiceImplement implements PaypalService {
    private final PaypalConfig paypalConfig;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaypalResponse createOrder(PaypalCreationRequest request, String returnUrl, String cancelUrl) {
        try {
            PayPalHttpClient payPalHttpClient = paypalConfig.payPalHttpClient();
            Invoice invoice = null;
            if (request.getInvoiceId() != null) {
                Optional<Invoice> invoiceOpt = invoiceRepository.findById(request.getInvoiceId());
                if (invoiceOpt.isPresent()) {
                    invoice = invoiceOpt.get();
                    invoice.setStatus(InvoiceStatus.PENDING);
                    invoiceRepository.save(invoice);
                }
            }
            Payment payment = null;
            if (invoice != null) {
                payment = Payment.builder()
                    .invoice(invoice)
                    .paymentMethod(PaymentMethod.PAYPAL)
                    .amountPaid(BigDecimal.valueOf(request.getAmount()))
                    .status(PaymentStatus.PENDING)
                    .notes(request.getDescription())
                    .build();
                payment = paymentRepository.save(payment);
            }
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");
            ApplicationContext applicationContext = new ApplicationContext()
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .brandName("BookingCareApp")
                    .landingPage("LOGIN")
                    .userAction("PAY_NOW");
            orderRequest.applicationContext(applicationContext);
            List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
            purchaseUnits.add(new PurchaseUnitRequest()
                    .amountWithBreakdown(new AmountWithBreakdown()
                            .currencyCode(request.getCurrency() != null ? request.getCurrency() : "USD")
                            .value(String.format("%.2f", request.getAmount()))));
            orderRequest.purchaseUnits(purchaseUnits);
            OrdersCreateRequest req = new OrdersCreateRequest().requestBody(orderRequest);
            HttpResponse<Order> response = payPalHttpClient.execute(req);
            Order order = response.result();
            String orderId = order.id();
            if (payment != null && orderId != null) {
                payment.setTransactionId(orderId);
                paymentRepository.save(payment);
            }
            for (LinkDescription link : order.links()) {
                if ("approve".equals(link.rel())) {
                    return PaypalResponse.builder()
                            .approvalUrl(link.href())
                            .status("PENDING")
                            .message("Created PayPal payment successfully")
                            .build();
                }
            }
            return PaypalResponse.builder()
                    .approvalUrl(null)
                    .status("FAILED")
                    .message("No approval link found")
                    .build();
        } catch (Exception e) {
            return PaypalResponse.builder()
                    .approvalUrl(null)
                    .status("FAILED")
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public boolean captureOrder(String token, String invoiceId) {
        try {
            PayPalHttpClient payPalHttpClient = paypalConfig.payPalHttpClient();
            OrdersCaptureRequest request = new OrdersCaptureRequest(token);
            HttpResponse<Order> response = payPalHttpClient.execute(request);
            Order order = response.result();
            boolean success = "COMPLETED".equals(order.status());
            if (success && invoiceId != null) {
                Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
                invoiceOpt.ifPresent(invoice -> {
                    invoice.setStatus(InvoiceStatus.PAID);
                    invoiceRepository.save(invoice);
                });
                String orderId = order.id();
                Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(orderId);
                paymentOpt.ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setPaymentDate(LocalDateTime.now());
                    payment.setNotes((payment.getNotes() != null ? payment.getNotes() : "") + " - PayPal Success");
                    paymentRepository.save(payment);
                });
            }
            return success;
        } catch (Exception e) {
            return false;
        }
    }
}
