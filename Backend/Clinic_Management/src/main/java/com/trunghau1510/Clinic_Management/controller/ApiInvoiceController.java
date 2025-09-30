package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.InvoiceCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.InvoiceUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.InvoiceResponse;
import com.trunghau1510.Clinic_Management.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiInvoiceController {
    InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceCreationRequest request) {
        InvoiceResponse invoice = invoiceService.addInvoice(request);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoice)
                .message("Tạo hóa đơn thành công")
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<InvoiceResponse> updateInvoice(@PathVariable String id, @Valid @RequestBody InvoiceUpdateRequest request) {
        InvoiceResponse invoice = invoiceService.updateInvoice(id, request);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoice)
                .message("Cập nhật hóa đơn thành công")
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ApiResponse<InvoiceResponse> getInvoiceById(@PathVariable String id) {
        InvoiceResponse invoice = invoiceService.getInvoiceById(id);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoice)
                .message("Lấy thông tin hóa đơn thành công")
                .build();
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ApiResponse<InvoiceResponse> getInvoiceByAppointmentId(@PathVariable String appointmentId) {
        InvoiceResponse invoice = invoiceService.getInvoiceByAppointmentId(appointmentId);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoice)
                .message("Lấy hóa đơn theo lịch hẹn thành công")
                .build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ApiResponse<List<InvoiceResponse>> getInvoicesByUserId(@PathVariable String userId) {
        List<InvoiceResponse> invoices = invoiceService.getInvoicesByUserId(userId);
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(invoices)
                .message("Lấy danh sách hóa đơn theo người dùng thành công")
                .count(invoices.size())
                .build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<InvoiceResponse> updateInvoiceStatus(@PathVariable String id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        InvoiceResponse invoice = invoiceService.updateInvoiceStatus(id, status);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoice)
                .message("Cập nhật trạng thái hóa đơn thành công")
                .build();
    }

}
