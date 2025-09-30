package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.DoctorSearchRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.DoctorResponse;
import com.trunghau1510.Clinic_Management.service.DoctorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE , makeFinal=true)
public class ApiDoctorController {

    DoctorService doctorService;

    @PreAuthorize("hasAuthority('DOCTOR')")
    @PutMapping("/{doctorId}")
    public ApiResponse<DoctorResponse> updateDoctor(@PathVariable("doctorId") String doctorId, @RequestBody DoctorUpdateRequest request) {
        DoctorResponse response = doctorService.updateDoctor(doctorId, request);
        return ApiResponse.<DoctorResponse>builder()
                .result(response)
                .message("Cập nhật thông tin bác sĩ thành công")
                .build();
    }

    @GetMapping("")
    public ApiResponse<Page<DoctorResponse>> getDoctors(Pageable pageable) {
        Page<DoctorResponse> doctors = doctorService.getDoctors(pageable);
        return ApiResponse.<Page<DoctorResponse>>builder()
                .result(doctors)
                .message("Lấy danh sách bác sĩ thành công")
                .count(doctors.getTotalElements())
                .build();
    }

    @GetMapping("/{doctorId}")
    public ApiResponse<DoctorResponse> getDoctorById(@PathVariable("doctorId") String doctorId) {
        DoctorResponse response = doctorService.getDoctorById(doctorId);
        return ApiResponse.<DoctorResponse>builder()
                .result(response)
                .message("Lấy thông tin bác sĩ thành công")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<Page<DoctorResponse>> searchDoctorsByKeyword(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        DoctorSearchRequest request = DoctorSearchRequest.builder().keyword(keyword).build();
        Page<DoctorResponse> doctors = doctorService.searchDoctorsByKeyword(request, pageable);
        return ApiResponse.<Page<DoctorResponse>>builder()
                .result(doctors)
                .message("Tìm kiếm bác sĩ thành công")
                .count(doctors.getTotalElements())
                .build();
    }
}
