package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.request.DoctorScheduleCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.DoctorScheduleUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.response.ApiResponse;
import com.trunghau1510.Clinic_Management.dto.response.DoctorScheduleResponse;
import com.trunghau1510.Clinic_Management.service.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiDoctorScheduleController {

    DoctorScheduleService doctorScheduleService;

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/doctor_schedule")
    public ApiResponse<DoctorScheduleResponse> registerSchedule(@Valid @RequestBody DoctorScheduleCreationRequest request) {
        return ApiResponse.<DoctorScheduleResponse>builder()
                .result(doctorScheduleService.registerDoctorSchedule(request))
                .build();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor_schedule/{doctorId}")
    public ApiResponse<List<DoctorScheduleResponse>> getDoctorScheduleById(@PathVariable String doctorId) {
        return ApiResponse.<List<DoctorScheduleResponse>>builder()
                .result(doctorScheduleService.getDoctorScheduleById(doctorId))
                .build();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PatchMapping("/doctor_schedule/{id}")
    public ApiResponse<DoctorScheduleResponse> updateSchedule(@PathVariable String id,
                                                             @Valid @RequestBody DoctorScheduleUpdateRequest request) {
        return ApiResponse.<DoctorScheduleResponse>builder()
                .result(doctorScheduleService.updateDoctorSchedule(id, request))
                .build();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/doctor_schedule/{id}")
    public ApiResponse<Void> deleteSchedule(@PathVariable String id) {
        doctorScheduleService.deleteSchedule(id);
        return ApiResponse.<Void>builder()
                .build();
    }
}
