package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.config.VNPayConfig;
import com.trunghau1510.Clinic_Management.dto.request.VNPayCreationRequest;
import com.trunghau1510.Clinic_Management.dto.response.VNPayResponse;
import com.trunghau1510.Clinic_Management.entity.Invoice;
import com.trunghau1510.Clinic_Management.entity.Payment;
import com.trunghau1510.Clinic_Management.entity.enums.PaymentMethod;
import com.trunghau1510.Clinic_Management.entity.enums.PaymentStatus;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.exception.ErrorCode;
import com.trunghau1510.Clinic_Management.repository.InvoiceRepository;
import com.trunghau1510.Clinic_Management.repository.PaymentRepository;
import com.trunghau1510.Clinic_Management.service.VNPayService;
import com.trunghau1510.Clinic_Management.utils.VNPayHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayServiceImplement implements VNPayService {

    private final VNPayConfig vnPayConfig;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public VNPayResponse createPayment(VNPayCreationRequest request, HttpServletRequest httpRequest) {
        try {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_EXISTS));

            // Tạo Payment record với status PENDING
            Payment payment = Payment.builder()
                .invoice(invoice)
                .paymentMethod(PaymentMethod.VNPAY)
                .amountPaid(BigDecimal.valueOf(request.getAmount()))
                .status(PaymentStatus.PENDING)
                .notes(request.getOrderInfo())
                .build();

            Payment savedPayment = paymentRepository.save(payment);

            // Tạo VNPay parameters
            String vnp_Version = vnPayConfig.getVnpVersion();
            String vnp_Command = vnPayConfig.getVnpCommand();
            String vnp_TmnCode = vnPayConfig.getVnpTmnCode();
            String vnp_Amount = String.valueOf(request.getAmount() * 100);
            String vnp_CurrCode = "VND";
            String vnp_TxnRef = generateTxnRef(request.getInvoiceId(), savedPayment.getId());
            String vnp_OrderInfo = removeVietnameseChars(request.getOrderInfo());
            String vnp_OrderType = vnPayConfig.getVnpOrderType();
            String vnp_Locale = "vn";
            String vnp_ReturnUrl = vnPayConfig.getVnpReturnUrl();
            String vnp_IpAddr = getClientIpAddress(httpRequest);

            // Tạo thời gian
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());

            // Build parameters map
            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", vnp_Amount);
            vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", vnp_OrderType);
            vnp_Params.put("vnp_Locale", vnp_Locale);
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Tạo secure hash sử dụng utility class
            String vnp_SecureHash = VNPayHashUtil.createVNPayHash(vnp_Params, vnPayConfig.getSecretKey());

            StringBuilder paymentUrl = new StringBuilder();
            paymentUrl.append(vnPayConfig.getVnpPayUrl()).append("?");

            // Encode parameters cho URL
            List<String> queryParams = new ArrayList<>();
            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value != null && !value.trim().isEmpty()) {
                    try {
                        String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
                        queryParams.add(key + "=" + encodedValue);
                    } catch (Exception e) {
                        queryParams.add(key + "=" + value);
                    }
                }
            }

            paymentUrl.append(String.join("&", queryParams));
            paymentUrl.append("&vnp_SecureHash=").append(vnp_SecureHash);

            savedPayment.setTransactionId(vnp_TxnRef);
            paymentRepository.save(savedPayment);

            return VNPayResponse.builder()
                    .code("00")
                    .message("success")
                    .paymentUrl(paymentUrl.toString())
                    .build();

        } catch (Exception e) {
            throw new AppException(ErrorCode.PAYMENT_CREATION_FAILED);
        }
    }

    @Override
    @Transactional
    public String handlePaymentReturn(HttpServletRequest request) {
        // XỬ LÝ VNPAY CALLBACK

        Map<String, String> fields = new TreeMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
        String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");

        boolean isValidSignature = VNPayHashUtil.verifyVNPaySignature(fields, vnp_SecureHash, vnPayConfig.getSecretKey());

        if (!isValidSignature) {
            return "INVALID";
        }

        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(vnp_TxnRef);

        try {
            if ("00".equals(vnp_TransactionStatus)) {
                // Thanh toán thành công
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setPaymentDate(LocalDateTime.now());
                    payment.setNotes(payment.getNotes() + " - VNPay Success: " + vnp_TransactionNo);
                    paymentRepository.save(payment);

                    // Cập nhật trạng thái hóa đơn
                    Invoice invoice = payment.getInvoice();
                    invoice.setStatus(com.trunghau1510.Clinic_Management.entity.enums.InvoiceStatus.PAID);
                    invoiceRepository.save(invoice);
                }
                return "SUCCESS";
            } else {
                // Thanh toán thất bại
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    payment.setStatus(PaymentStatus.FAILED);
                    payment.setNotes(payment.getNotes() + " - VNPay Failed: " + vnp_ResponseCode);
                    paymentRepository.save(payment);
                }
                return "FAILED";
            }
        } catch (Exception e) {
            // Đã bỏ log.error
            return "INVALID";
        }
    }

    private String generateTxnRef(String invoiceId, String paymentId) {
        return "PAY_" + invoiceId.substring(0, 8) + "_" + paymentId.substring(0, 8) + "_" + System.currentTimeMillis();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0];
        }

        String remoteAddr = request.getRemoteAddr();
        // Convert IPv6 localhost to IPv4
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "::1".equals(remoteAddr)) {
            return "127.0.0.1";
        }

        return remoteAddr != null ? remoteAddr : "127.0.0.1";
    }

    private String removeVietnameseChars(String input) {
        if (input == null) return null;

        // Bỏ dấu tiếng Việt để đảm bảo tương thích với VNPay
        String normalized = input
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[ÌÍỊỈĨ]", "I")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U")
                .replaceAll("[ỳýỵỹ]", "y")
                .replaceAll("[ỲÝỴỶỸ]", "Y")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D");

        // Chỉ giữ lại ký tự alphanumeric và một số ký tự đặc biệt
        return normalized.replaceAll("[^a-zA-Z0-9\\s\\-_.,]", "");
    }
}
