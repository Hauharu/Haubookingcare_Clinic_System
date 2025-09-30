package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.DoctorLicenseCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorLicenseUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.DoctorLicenseResponse;
import com.trunghau1510.Clinic_Management.service.DoctorLicenseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor-licenses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiDoctorLicenseController {

    DoctorLicenseService doctorLicenseService;

    @PostMapping
    public ApiResponse<DoctorLicenseResponse> createLicense(@RequestBody @Valid DoctorLicenseCreationRequest request) {
        DoctorLicenseResponse response = doctorLicenseService.createLicense(request);
        return ApiResponse.<DoctorLicenseResponse>builder()
                .message("Tạo chứng chỉ hành nghề thành công")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<DoctorLicenseResponse> updateLicense(@PathVariable String id, @RequestBody @Valid DoctorLicenseUpdateRequest request) {
        DoctorLicenseResponse response = doctorLicenseService.updateLicense(id, request);
        return ApiResponse.<DoctorLicenseResponse>builder()
                .message("Cập nhật chứng chỉ hành nghề thành công")
                .result(response)
                .build();
    }

    @GetMapping("/{doctorId}")
    public ApiResponse<DoctorLicenseResponse> getLicenseByDoctorId(@PathVariable String doctorId) {
        DoctorLicenseResponse response = doctorLicenseService.getLicenseByDoctorId(doctorId);
        return ApiResponse.<DoctorLicenseResponse>builder()
                .message(response == null ? "Chưa có chứng chỉ hành nghề" : "Lấy chứng chỉ hành nghề thành công")
                .result(response)
                .build();
    }
}