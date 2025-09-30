package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.HealthRecordCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.HealthRecordUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.HealthRecordResponse;
import com.trunghau1510.Clinic_Management.service.HealthRecordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiHealthRecordController {

    HealthRecordService healthRecordService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<HealthRecordResponse> createHealthRecord(@Valid @RequestBody HealthRecordCreationRequest request) {
        HealthRecordResponse healthRecord = healthRecordService.addHealthRecord(request);
        return ApiResponse.<HealthRecordResponse>builder()
                .result(healthRecord)
                .message("Tạo hồ sơ sức khỏe thành công")
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<HealthRecordResponse> updateHealthRecord(@PathVariable String id, @Valid @RequestBody HealthRecordUpdateRequest request) {
        HealthRecordResponse healthRecord = healthRecordService.updateHealthRecord(id, request);
        return ApiResponse.<HealthRecordResponse>builder()
                .result(healthRecord)
                .message("Cập nhật hồ sơ sức khỏe thành công")
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<HealthRecordResponse>> getHealthRecordsByUserId(@PathVariable String userId) {
        List<HealthRecordResponse> healthRecords = healthRecordService.getHealthRecordsByUserId(userId);
        return ApiResponse.<List<HealthRecordResponse>>builder()
                .result(healthRecords)
                .message("Lấy danh sách hồ sơ sức khỏe thành công")
                .count(healthRecords.size())
                .build();
    }

    @GetMapping("/appointment/{appointmentId}")
    public ApiResponse<HealthRecordResponse> getRecordByAppointmentId(@PathVariable String appointmentId) {
        HealthRecordResponse healthRecord = healthRecordService.getHealthRecordByAppointmentId(appointmentId);
        return ApiResponse.<HealthRecordResponse>builder()
                .result(healthRecord)
                .message("Lấy hồ sơ sức khỏe theo lịch hẹn thành công")
                .build();
    }
}
